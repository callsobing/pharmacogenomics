package C4lab;

import htsjdk.tribble.readers.LineIteratorImpl;
import htsjdk.tribble.readers.LineReaderUtil;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;
import java.io.*;


public class VcfReaderYuta {

    //main class盡量簡化，以方便之後的使用彈性
    public static void main(String[] args) throws IOException {
        final String vcfPath = args[0];

        //rsID
        String[] rsIDList = {
                "rs587638290",
                "rs587736341",
                "rs534965407",
                "rs9609649",
                "rs5845047",
                "rs80690",
                "rs137506",
                "rs138355780",
        };

        //
        printAltAllelesLongerThen(100, vcfPath);
        printAllAllelsWithRSID(vcfPath);
        printRSIDequals(rsIDList, vcfPath);

    }

    //以mathod來實作不同功能（包含讀檔程序）
    public static void printAltAllelesLongerThen(int length, String vcfPath) throws IOException {

        //讀檔前置作業、參數宣告
        BufferedReader schemaReader = new BufferedReader(new FileReader(vcfPath));
        VCFCodec vcfCodec = new VCFCodec();
        String line;
        String headerLine = "";
        VariantContext vctx;

        //單行讀檔
        while ((line = schemaReader.readLine()) != null) {

            //分離Information field
            if (line.startsWith("#")) {
                headerLine = headerLine.concat(line).concat("\n");
                continue;
            }
            vcfCodec.readActualHeader(new LineIteratorImpl(LineReaderUtil.fromStringReader(
                    new StringReader(headerLine), LineReaderUtil.LineReaderOption.SYNCHRONOUS)));

            //print 出 AltAlleles 長度超過100者
            if (!line.startsWith("#")) {
                vctx = vcfCodec.decode(line);
                if (vctx.getAlternateAlleles().get(0).length() > length) {
                    System.out.println(
                            "rsID: " + vctx.getID() +
                                    " length: " + vctx.getAlternateAlleles().get(0).length() +
                                    " ref: " + vctx.getReference() +
                                    " alt:" + vctx.getAlternateAlleles().get(0) +
                                    " GT: " + vctx.getGenotype(vctx.getSampleNamesOrderedByName().get(0)).getGenotypeString());
                }
            }
        }
    }

    //以rsID搜尋variants
    public static void printRSIDequals(String[] rsIDList, String vcfPath) throws IOException {

        //讀檔前置作業、參數宣告
        BufferedReader schemaReader = new BufferedReader(new FileReader(vcfPath));
        VCFCodec vcfCodec = new VCFCodec();
        String line;
        String headerLine = "";
        VariantContext vctx;
        int[] rsIDCount = new int[rsIDList.length];

        //單行讀檔迴圈
        while ((line = schemaReader.readLine()) != null) {

            //分離Information field
            if (line.startsWith("#")) {
                headerLine = headerLine.concat(line).concat("\n");
                continue;
            }
            vcfCodec.readActualHeader(new LineIteratorImpl(LineReaderUtil.fromStringReader(
                    new StringReader(headerLine), LineReaderUtil.LineReaderOption.SYNCHRONOUS)));

            for (int i=0; i<rsIDList.length; i++) {
                if (!line.startsWith("#")) {
                    vctx = vcfCodec.decode(line);
                    if (vctx.getID().equals(rsIDList[i])) {
                        rsIDCount[i]++;
                        System.out.println(
                                "|rsIDCount: " + rsIDCount[i]
                                        + "| |rsID: " + vctx.getID()
                                        + "| |POS: " + vctx.getEnd()
                                        + "| |ref: " + vctx.getReference()
                                        + "| |alt(getAlternateAlleles): " + vctx.getAlternateAlleles().get(0)
                                        + "| |GT: " + vctx.getGenotype(vctx.getSampleNamesOrderedByName().get(0)).getGenotypeString()
                                        + "|");
                    }
                }
            }
        }
    }

    //print出所有擁有rsID的variants：
    public static void printAllAllelsWithRSID(String vcfPath) throws IOException {

        //讀檔前置作業、參數宣告
        BufferedReader schemaReader = new BufferedReader(new FileReader(vcfPath));
        VCFCodec vcfCodec = new VCFCodec();
        String line;
        String headerLine = "";
        VariantContext vctx;

        //單行讀檔
        while ((line = schemaReader.readLine()) != null) {

            //分離Information field
            if (line.startsWith("#")) {
                headerLine = headerLine.concat(line).concat("\n");
                continue;
            }
            vcfCodec.readActualHeader(new LineIteratorImpl(LineReaderUtil.fromStringReader(
                    new StringReader(headerLine), LineReaderUtil.LineReaderOption.SYNCHRONOUS)));

            if (!line.startsWith("#")) {
                vctx = vcfCodec.decode(line);
                if (!(vctx.getID().equals("."))) {
                    System.out.println(
                            "|rsID: " + vctx.getID()
                                    + "| |ref: " + vctx.getReference()
                                    + "| |alt(getAlternateAlleles): " + vctx.getAlternateAlleles().get(0)
                                    + "| |GT: " + vctx.getGenotype(vctx.getSampleNamesOrderedByName().get(0)).getGenotypeString()
                                    + "| |"
                    );
                }
            }
        }
    }
}

