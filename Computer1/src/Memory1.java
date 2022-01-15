
public class Memory1 {

    // SP�� header, codeSegment ���� ��
    private short memory[];

    public Memory1() {
        this.memory = new short[512];
    }

    public short load(short mar) {
        short data = this.memory[mar];

        // mbr return
        return data;
    }

    public void store(short mar, short mbr) {
        this.memory[mar] = mbr;
    }

    public int allocateMemory(int loaderInput) {
        // loaderInput = (sizeHeader + sizeCodeSegment / 2 + sizeDataSegment / 2)
        int startAddress = 0;
        if (this.memory == null) {
            return 0;
        } else {
            return startAddress;
        }
    }
}
