package C4lab;

import htsjdk.tribble.readers.LineIteratorImpl;
import htsjdk.tribble.readers.LineReaderUtil;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


public class VcfReaderYuta {

    /* main class盡量簡化，以方便之後的使用彈性 */
    public static void main(String[] args) throws IOException {
        final String vcfPath = args[0];



        /* 2016/03/31 hw1: 計算 chr22 vcf裡面 SAS, EAS的 allele frequency都高於 total的AF 的個數有幾個 */
        System.out.println("\nTask:計算 chr22 vcf裡面 SAS, EAS的 allele frequency都高於 total的AF 的個數有幾個...(please wait)");
        System.out.println("SAS_AF與EAS_AF皆大於AF的Variants個數：" + AFComparison(vcfPath)); // UNCOMMENT TO RUN!

        /* 2016/03/31 hw2: 找出(算出)八個rsID分別對應的Allele frequency */
        System.out.println("\nTask:找出(算出)TargetRSIDList中每個rsID對應的Allele frequency...(please wait)");
        String[] TargetRSIDList = {
                "rs587638290",
                "rs587736341",
                "rs534965407",
                "rs9609649",
                "rs5845047",
                "rs80690",
                "rs137506",
                "rs138355780",
        };
        HashMap<String,Double> rsID_AF = new HashMap<String, Double>();
        for(int i=0;i<TargetRSIDList.length;i++) {
            rsID_AF.put(TargetRSIDList[i], -1.0);
        }
        System.out.println("Target rsIDs: "+rsID_AF);
        FindAFsForRSIDs(vcfPath,rsID_AF);
        System.out.println("AF Results: "+rsID_AF);

        /* 2016/03/24 hw: 找出長度超過100的Allele */
//        printAltAllelesLongerThen(100, vcfPath);  // UNCOMMENT TO RUN!

        /* 2016/03/17 hw: 找出所有rsID已知的Allele */
//        printAllAllelesWithRSID(vcfPath); // UNCOMMENT TO RUN!

        /* 2016/03/24 hw:  */
//        printRSIDEquals(TargetRSIDList, vcfPath); // UNCOMMENT TO RUN!

        System.out.println("Process Finished.");
    }


    /*讀vcf檔，找出SAS_AF與EAS_AF皆大於AF的Variants，並回報符合條件的variants個數*/
    public static int AFComparison(String vcfPath) throws IOException{

        /* 讀檔前置作業、參數宣告 */
        BufferedReader schemaReader = new BufferedReader(new FileReader(vcfPath));
        VCFCodec vcfCodec = new VCFCodec();
        String line;
        String headerLine = "";
        VariantContext vctx;
        int NFreqHigher = 0;

        /* 一行行讀檔 */
        while ((line = schemaReader.readLine()) != null) {

            /* 先將metadata lines的部分存到headerLine */
            if (line.startsWith("#")) {
                headerLine = headerLine.concat(line).concat("\n");continue;
            }
            vcfCodec.readActualHeader(new LineIteratorImpl(LineReaderUtil.fromStringReader(
                    new StringReader(headerLine), LineReaderUtil.LineReaderOption.SYNCHRONOUS)));


            /* 開始對data lines(每一筆variants)的操作： */
            if (!line.startsWith("#")) {
                vctx = vcfCodec.decode(line);

                /* [狀況I] ID中沒有分號且AF沒有逗號者 */
                if(!vctx.getID().contains(";")) {
                    if (!vctx.getAttributeAsString("AF", "-1.0").contains(",")) {
                        boolean FreqHigher = ((vctx.getAttributeAsDouble("SAS_AF", -1.0) > vctx.getAttributeAsDouble("AF", -1.0))
                                && (vctx.getAttributeAsDouble("EAS_AF", -1.0) > vctx.getAttributeAsDouble("AF", -1.0)));

                        /* UNCOMMENT TO VIEW PROCESS */
//                        System.out.println(
//                                "I  "
//                                        + " \trsID: " + vctx.getID() + " \t"
//                                        + " \tAF: " + vctx.getAttributeAsDouble("AF", -1.0)
//                                        + " \tSAS_AF: " + vctx.getAttributeAsDouble("SAS_AF", -1.0)
//                                        + " \tEAS_AF=: " + vctx.getAttributeAsDouble("EAS_AF", -1.0)
//                                        + " \tHigher Frequency? " + FreqHigher
//                        );

                        /* 找到SAS_AF與EAS_AF皆大於AF者，則計數 */
                        if (FreqHigher) {
                            NFreqHigher++;
                        }
                    }

                    /* [狀況II] 若AF的值有逗號，代表有多個alt（因此有多個allele freq），在此以ArrayList的forEach處理 */
                    else if (vctx.getAttributeAsString("AF", "-1.0").contains(",")) {
                        ArrayList<String> AFlist = (ArrayList) vctx.getAttribute("AF", "EMPTY");
                        ArrayList<String> SAS_AFlist = (ArrayList) vctx.getAttribute("SAS_AF", "EMPTY");
                        ArrayList<String> EAS_AFlist = (ArrayList) vctx.getAttribute("EAS_AF", "EMPTY");
                        for (int i = 0; i < AFlist.size(); i++) {
                            boolean FreqHigher = ((Double.parseDouble(SAS_AFlist.get(i)) > Double.parseDouble(AFlist.get(i)))
                                    && (Double.parseDouble(EAS_AFlist.get(i)) > Double.parseDouble(AFlist.get(i))));

                            /* UNCOMMENT TO VIEW PROCESS */
//                            System.out.println(
//                                    "II  "
//                                            + " \trsID: " + vctx.getID() + " \t"
//                                            + " \tAF: " + AFlist.get(i)
//                                            + " \tSAS_AF: " + SAS_AFlist.get(i)
//                                            + " \tEAS_AF=: " + EAS_AFlist.get(i)
//                                            + " \tHigher Frequency? " + FreqHigher
//
//                            );
                            if (FreqHigher) {
                                NFreqHigher++;
                            }
                        }
                    }
                }

                /* [狀況III] 若ID欄有分號，代表同一位置有多個基因，在此處理 */
                if(vctx.getID().contains(";")){
                    String[] IDs = vctx.getID().split(";");
                    int N = IDs.length;
                    ArrayList<String> AFlist = (ArrayList)vctx.getAttribute("AF","EMPTY");
                    ArrayList<String> SAS_AFlist = (ArrayList)vctx.getAttribute("SAS_AF","EMPTY");
                    ArrayList<String> EAS_AFlist = (ArrayList)vctx.getAttribute("EAS_AF","EMPTY");
                    for (int i=0; i<AFlist.size(); i++) {
                        boolean FreqHigher = ((Double.parseDouble(SAS_AFlist.get(i)) > Double.parseDouble(AFlist.get(i)))
                                && (Double.parseDouble(EAS_AFlist.get(i)) > Double.parseDouble(AFlist.get(i))));

                        /* UNCOMMENT TO VIEW PROCESS */
//                        System.out.println(
//                                "III  "
//                                        + " \trsID: " + IDs[i] + " \t"
//                                        + " \tAF: " + AFlist.get(i)
//                                        + " \tSAS_AF: " + SAS_AFlist.get(i)
//                                        + " \tEAS_AF=: " + EAS_AFlist.get(i)
//                                        + " \tHigher Frequency? " + FreqHigher
//                        );
                        if (FreqHigher){
                            NFreqHigher++;
                        }
                    }
                }
            }
        }
        return NFreqHigher;
    }

    /*讀vcf檔，找出TargetRSIDList中每個rsID對應的Allele frequency*/
    public static int FindAFsForRSIDs(String vcfPath, HashMap<String, Double> rsID_AF) throws IOException{

        /* 讀檔前置作業、參數宣告 */
        BufferedReader schemaReader = new BufferedReader(new FileReader(vcfPath));
        VCFCodec vcfCodec = new VCFCodec();
        String line;
        String headerLine = "";
        VariantContext vctx;
        int size = rsID_AF.size();
        int completed=0;

        /* 一行行讀檔 */
        while ((line = schemaReader.readLine()) != null) {

            /* 先將metadata lines的部分存到headerLine */
            if (line.startsWith("#")) {
                headerLine = headerLine.concat(line).concat("\n");continue;
            }
            vcfCodec.readActualHeader(new LineIteratorImpl(LineReaderUtil.fromStringReader(
                    new StringReader(headerLine), LineReaderUtil.LineReaderOption.SYNCHRONOUS)));


            if (!line.startsWith("#")) {
                vctx = vcfCodec.decode(line);
                /* ============================================= */
                /* |開始對data lines(vctx代表每一筆variants)的操作| */
                /* ============================================= */




                if(rsID_AF.isEmpty()){System.out.println("Empty rsIDs target list!");return -1;}
                for (String key : rsID_AF.keySet()){
                    if (vctx.getID().equals(key)){
                        completed++;
                        System.out.printf("Completed %d of %d.\n",completed,size);
                        /* 手算Allele frequency卡關中QQ，計算AF先用.getAttribute */
//                        rsID_AF.put(key,CalAF(vctx));
                        rsID_AF.put(key,vctx.getAttributeAsDouble("AF",-100.0));
                    }
                }

                /* ============================================= */

            }
        }
        return 1;
    }

    /*(施工中)讀vctx檔，計算該筆variant的allele frequency*/
    public static double CalAF(VariantContext vctx){
        int total = vctx.getCalledChrCount();
        int NAlt = 0;
        Allele altAllele = vctx.getAlternateAllele(0);
        Set<String> samples = vctx.getSampleNames();
        for (String sample: samples){
            System.out.println(vctx.getGenotype(sample));   //test
            System.out.println(vctx.getGenotype(sample).countAllele(altAllele));    //test
            NAlt+=vctx.getGenotype(sample).countAllele(altAllele);
        }
        return 0.0;
    }

    /*讀vcf檔，並print出長度超過length的variants*/
    public static void printAltAllelesLongerThen(int length, String vcfPath) throws IOException {

        //讀檔前置作業、參數宣告
        BufferedReader schemaReader = new BufferedReader(new FileReader(vcfPath));
        VCFCodec vcfCodec = new VCFCodec();
        String line;
        String headerLine = "";
        VariantContext vctx;

        //單行讀檔
        while ((line = schemaReader.readLine()) != null) {

            //分離Metadata lines
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

    /*讀vcf檔，並以rsIDList搜尋variants*/
    public static void printRSIDEquals(String[] rsIDList, String vcfPath) throws IOException {

        //讀檔前置作業、參數宣告
        BufferedReader schemaReader = new BufferedReader(new FileReader(vcfPath));
        VCFCodec vcfCodec = new VCFCodec();
        String line;
        String headerLine = "";
        VariantContext vctx;
        int[] rsIDCount = new int[rsIDList.length];

        //單行讀檔迴圈
        while ((line = schemaReader.readLine()) != null) {

            //分離Metadata lines
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

    /*讀vcf檔，並print出所有擁有rsID的variants*/
    public static void printAllAllelesWithRSID(String vcfPath) throws IOException {

        //讀檔前置作業、參數宣告
        BufferedReader schemaReader = new BufferedReader(new FileReader(vcfPath));
        VCFCodec vcfCodec = new VCFCodec();
        String line;
        String headerLine = "";
        VariantContext vctx;

        //單行讀檔
        while ((line = schemaReader.readLine()) != null) {

            //分離Metadata lines
            if (line.startsWith("#")) {
                headerLine = headerLine.concat(line).concat("\n");
                continue;
            }
            vcfCodec.readActualHeader(new LineIteratorImpl(LineReaderUtil.fromStringReader(
                    new StringReader(headerLine), LineReaderUtil.LineReaderOption.SYNCHRONOUS)));

            if (!line.startsWith("#")) {
                vctx = vcfCodec.decode(line);
                if (!(vctx.getID() == ".")) {
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

    /*讀進vcf檔並對data lines進行操作的template*/
    public static int template(String vcfPath) throws IOException{

        /* 讀檔前置作業、參數宣告 */
        BufferedReader schemaReader = new BufferedReader(new FileReader(vcfPath));
        VCFCodec vcfCodec = new VCFCodec();
        String line;
        String headerLine = "";
        VariantContext vctx;

        /* 一行行讀檔 */
        while ((line = schemaReader.readLine()) != null) {

            /* 先將metadata lines的部分存到headerLine */
            if (line.startsWith("#")) {
                headerLine = headerLine.concat(line).concat("\n");continue;
            }
            vcfCodec.readActualHeader(new LineIteratorImpl(LineReaderUtil.fromStringReader(
                    new StringReader(headerLine), LineReaderUtil.LineReaderOption.SYNCHRONOUS)));


            if (!line.startsWith("#")) {
                vctx = vcfCodec.decode(line);
                /* ============================================= */
                /* |開始對data lines(vctx代表每一筆variants)的操作| */
                /* ============================================= */

                //code here
                //
                //

                /* ============================================= */

            }
        }
        return 0;
    }

}


