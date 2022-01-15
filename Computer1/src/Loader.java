import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Loader {

    private CPU1 cpu;
    private Memory1 memory;

    int sizeHeader;
    int sizeCodeSegment;
    int sizeDataSegment;
    int startAddress;

    private void loadHeader(Scanner sc) {
        String lineDataSegmentSize = sc.nextLine();
        String lineCodeSegmentSize = sc.nextLine();

        this.sizeHeader = 4;
        this.sizeDataSegment = this.decodeLine(lineDataSegmentSize); // 12
        // 0���� ����
        this.sizeCodeSegment = this.decodeLine(lineCodeSegmentSize); // 46

        this.memory.store((short) 0, (short) sizeDataSegment);
        this.memory.store((short) 1, (short) sizeCodeSegment);

        this.startAddress = this.memory.allocateMemory(sizeHeader + (sizeCodeSegment / 2) + (sizeDataSegment / 2));

        this.cpu.setPC((short) (startAddress + (sizeHeader / 2)));
        this.cpu.setSP((short) (startAddress + sizeHeader / 2 + sizeCodeSegment));

    }

    private short decodeLine(String line) {
        StringTokenizer st = new StringTokenizer(line);
        short sDigit = (short) Integer.parseInt(st.nextToken(), 16);

        ArrayList<String> waste = new ArrayList<String>();
        while (st.hasMoreTokens()) {
            waste.add(st.nextToken());
        }

        return sDigit;
    }

    private void loadBody(Scanner sc) {
        short pc = (short) (startAddress + (sizeHeader / 2));
        while (sc.hasNext()) {
            String line = sc.nextLine();
            short sLine = this.decodeLine(line);
            memory.store((short) (pc), sLine);
            pc++;
        }
    }

    public void load(String fileName) {
        try {
            File file = new File("exe/" + fileName);
            Scanner sc = new Scanner(file);
            loadHeader(sc);
            loadBody(sc);

            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void associate(Memory1 memory, CPU1 cpu) {
        this.memory = memory;
        this.cpu = cpu;

    }

}
