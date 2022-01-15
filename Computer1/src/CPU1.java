public class CPU1 {
    // declaration

    /**
     * eHalt, eLDA, eLDC, eSTA, eADD, eSUB, eSUBC, eMUL, eDIV, eAND, eJMP, eJMPBZ,
     * eJMPEQ
     */
    private enum ERegister {
        eIR, eSP, ePC, eAC, eMBR, eMAR, eSR
    }

    private class ALU {
        private short temp;

        public short subtrack(short value) {
            return (short) (this.temp - value);
        }

        public void greaterThan() {

        }

        public void equal() {

        }

        public short multiple(short value) {
            return (short) (this.temp * value);
        }

        public short divide(short value) {
            return (short) (this.temp / value);
        }

        public short add(short value) {
            return (short) (this.temp + value);
        }

        public void store(short value) {
            this.temp = value;

        }
    }

    private class Register {
        protected short value;

        public short getValue() {
            return this.value;
        }

        public void setValue(short value) {
            this.value = value;
        }
    }

    private class CU {
        public boolean isBZ(Register sr) {
            if ((sr.getValue() & 0x4000) == 0) {
                return false;
            } else {
                return true;
            }
        }

        public boolean isEQ(Register sr) {
            if ((sr.getValue() & 0x8000) == 0) {
                return false;
            } else
                return true;
        }

        public boolean isBZEQ(Register sr) {
            if (this.isEQ(sr) || this.isBZ(sr)) {
                return false;
            } else
                return true;
        }
    }

    private enum EOpcode {
        eHalt, eLDA, eLDC, eSTA, eADDA, eADDC, eSUBA, eSUBC, eMULA, eMULC, eDIVA, eDIVC, eANDA, eJMP, eJMPBZ, eJMPBZEQ,
        eJMPEQ
    }

    private class IR extends Register {
        public short getOperator() {
            return (short) ((this.value & 0xff00) >> 8);
        }

        public short getOperand() {
            return (short) (this.value & 0x00ff);
        }
    }

    // component
    private ALU alu;
    private CU cu;
    Register registers[];

    public void setPC(short PC) {
        // �ʱ� pc�� ����
        this.registers[ERegister.ePC.ordinal()].setValue(PC);
    }

    public void setSP(short SP) {
        this.registers[ERegister.eSP.ordinal()].setValue(SP);
    }

    // association
    private Memory1 memory;

    // states
    private boolean bPowerOn;

    private boolean isPowerOn() {
        return this.bPowerOn;
    }

    public void setPowerOn() {
        this.bPowerOn = true;
        this.run();
    }

    public void shutdown() {
        this.bPowerOn = false;
    }

    // associate
    public void associate(Memory1 memory) {
        this.memory = memory;
    }

    // instructions
    private void Halt() {
        this.bPowerOn = false;
    }

    private void LDA() {
        // IR -> MAR
        this.registers[ERegister.eMAR.ordinal()]
                .setValue(((CPU1.IR) this.registers[ERegister.eIR.ordinal()]).getOperand());
        // Memory[MAR] -> MBR
        this.registers[ERegister.eMBR.ordinal()]
                .setValue(this.memory.load((short) (this.registers[ERegister.eMAR.ordinal()].getValue() / 2
                        + this.registers[ERegister.eSP.ordinal()].getValue())));
        // MBR -> AC
        this.registers[ERegister.eAC.ordinal()].setValue(this.registers[ERegister.eMBR.ordinal()].getValue());
    }

    private void LDC() {
        // IR -> MBR
        this.registers[ERegister.eMBR.ordinal()]
                .setValue(((CPU1.IR) this.registers[ERegister.eIR.ordinal()]).getOperand());

        // MBR -> AC
        this.registers[ERegister.eAC.ordinal()].setValue(this.registers[ERegister.eMBR.ordinal()].getValue());
    }

    private void STA() {
        // AC -> MBR
        this.registers[ERegister.eMBR.ordinal()].setValue(this.registers[ERegister.eAC.ordinal()].getValue());
        // IR -> MAR
        this.registers[ERegister.eMAR.ordinal()]
                .setValue(((CPU1.IR) this.registers[ERegister.eIR.ordinal()]).getOperand());
        // MBR -> memory[MAR]
        this.memory.store(
                (short) ((this.registers[ERegister.eMAR.ordinal()].getValue() / 2
                        + this.registers[ERegister.eSP.ordinal()].getValue())),
                this.registers[ERegister.eMBR.ordinal()].getValue());

        System.out.println("Store data : " + this.registers[ERegister.eMBR.ordinal()].getValue());
    }

    private void ADDA() {
        // AC -> ALU ��� ����
        this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());
        // �� �� �� AC�� �����
        this.LDA();
        // AC + ALU -> AC
        this.registers[ERegister.eAC.ordinal()]
                .setValue(this.alu.add(this.registers[ERegister.eAC.ordinal()].getValue()));
    }

    private void ADDC() {
        // AC -> ALU ��� ����
        this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());
        // �� �� �� AC�� �����
        this.LDC();
        // AC + ALU
        this.alu.add(this.registers[ERegister.eAC.ordinal()].getValue());
    }

    private void SUBA() {
        // AC -> ALU ��� ����
        this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());
        // �� �� �� AC�� �����
        this.LDA();
        // AC + ALU
        this.alu.subtrack(this.registers[ERegister.eAC.ordinal()].getValue());
    }

    private void SUBC() {
        // AC -> ALU ��� ����
        this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());

        this.LDC();
        this.registers[ERegister.eAC.ordinal()]
                .setValue(this.alu.subtrack(this.registers[ERegister.eAC.ordinal()].getValue()));
        ;
        System.out.println(this.registers[ERegister.eAC.ordinal()].getValue() + "  dd");

    }

    private void MULA() {
        // AC -> ALU ��� ����
        this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());

        this.LDA();
        // AC + ALU
        this.alu.multiple(this.registers[ERegister.eAC.ordinal()].getValue());
    }

    private void MULC() {
        // AC -> ALU ��� ����
        this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());

        this.LDC();
        // AC + ALU
        this.registers[ERegister.eAC.ordinal()]
                .setValue(this.alu.multiple(this.registers[ERegister.eAC.ordinal()].getValue()));
        ;
    }

    private void DIVA() {
        // AC -> ALU ��� ����
        this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());
        // ���� �� AC�� �����
        this.LDA();
        // AC + ALU
        this.registers[ERegister.eAC.ordinal()]
                .setValue(this.alu.divide(this.registers[ERegister.eAC.ordinal()].getValue()));
    }

    private void DIVC() {
        // AC -> ALU ��� ����
        this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());

        this.LDC();
        // AC + ALU
        this.registers[ERegister.eAC.ordinal()]
                .setValue(this.alu.divide(this.registers[ERegister.eAC.ordinal()].getValue()));
    }

    private void ANDA() {

    }

    private void JMP() {
        this.registers[ERegister.ePC.ordinal()]
                .setValue((short) (((CPU1.IR) this.registers[ERegister.eIR.ordinal()]).getOperand() + 1));
    }

    private void JMPBZ() {
//		SR sr = (SR) this.registers[ERegister.eSR.ordinal()];
        if (this.cu.isBZ(this.registers[ERegister.eSR.ordinal()])) {
            // IR -> PC
            this.registers[ERegister.ePC.ordinal()]
                    .setValue((short) (((CPU1.IR) this.registers[ERegister.eIR.ordinal()]).getOperand() + 1));
        }
    }

    private void JMPBZEQ() {
        if (this.cu.isBZEQ(this.registers[ERegister.eSR.ordinal()])) {
            this.registers[ERegister.ePC.ordinal()]
                    .setValue((short) (((CPU1.IR) this.registers[ERegister.eIR.ordinal()]).getOperand() + 1));
        }
    }

    private void JMPEQ() {

    }

    // constructor
    public CPU1() {
        this.cu = new CU();
        this.alu = new ALU();

        this.registers = new Register[ERegister.values().length];

        for (ERegister eRegister : ERegister.values()) {
            this.registers[eRegister.ordinal()] = new Register();
        }
        this.registers[ERegister.eIR.ordinal()] = new IR();
    }

    // method
    private void fetch() {
        // load next instruction from memory to IR
        // PC -> MAR
        this.registers[ERegister.eMAR.ordinal()].setValue(this.registers[ERegister.ePC.ordinal()].getValue());

        // !!! Memory -> MBR(data)
        this.registers[ERegister.eMBR.ordinal()]
                .setValue(this.memory.load(this.registers[ERegister.eMAR.ordinal()].getValue()));

        // MBR -> IR
        this.registers[ERegister.eIR.ordinal()].setValue(this.registers[ERegister.eMBR.ordinal()].getValue());
    }

    private void decode() {
        // eHalt, eLDA, eLDC, eSTA, eADD, eSUB, eSUBC, eMUL, eDIV, eANDA, eJMP, eJMPBZ,
        // eJMPEQ, eJMPBZEQ
    }

    private void execute() {
        System.out.println(
                "Instruction : " + EOpcode.values()[((CPU1.IR) this.registers[ERegister.eIR.ordinal()]).getOperator()]);

        switch (EOpcode.values()[((CPU1.IR) this.registers[ERegister.eIR.ordinal()]).getOperator()]) {
            case eHalt:
                this.Halt();
                break;
            case eLDA:
                this.LDA();
                PCPlus();
                break;
            case eLDC:
                this.LDC();
                PCPlus();
                break;
            case eSTA:
                this.STA();
                PCPlus();
                break;
            case eADDA:
                this.ADDA();
                PCPlus();
                break;
            case eADDC:
                this.ADDC();
                PCPlus();
                break;
            case eSUBA:
                this.SUBA();
                PCPlus();
                break;
            case eSUBC:
                this.SUBC();
                PCPlus();
                break;
            case eMULA:
                this.MULA();
                PCPlus();
                break;
            case eMULC:
                this.MULC();
                PCPlus();
                break;
            case eDIVA:
                this.DIVA();
                PCPlus();
                break;
            case eDIVC:
                this.DIVC();
                PCPlus();
                break;
            case eANDA:
                this.ANDA();
                PCPlus();
                break;
            case eJMP:
                this.JMP();
                break;
            case eJMPBZ:
                this.JMPBZ();
                break;
            case eJMPBZEQ:
                this.JMPBZEQ();
                break;
            case eJMPEQ:
                this.JMPEQ();
                break;
            default:
                break;
        }
    }

    private void PCPlus() {
        short temp = this.registers[ERegister.ePC.ordinal()].getValue();
        temp++;
        this.registers[ERegister.ePC.ordinal()].setValue(temp);
    }

    public void run() {
        while (isPowerOn()) {
            this.fetch();
            this.decode();
            this.execute();
        }
    }

    public static void main(String[] args) {

        CPU1 cpu = new CPU1();

        Memory1 memory = new Memory1();
        cpu.associate(memory);

        Loader loader = new Loader();
        loader.associate(memory, cpu);
        loader.load("Mid-term");

        cpu.setPowerOn();

    }

}