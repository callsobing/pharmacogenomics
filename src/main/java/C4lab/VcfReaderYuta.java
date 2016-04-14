package C4lab;

import htsjdk.tribble.readers.LineIteratorImpl;
import htsjdk.tribble.readers.LineReaderUtil;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;
import htsjdk.variant.vcf.VCFHeader;
import org.apache.tools.ant.taskdefs.Tar;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class VcfReaderYuta {

    /* main class盡量簡化，以方便之後的使用彈性 */
    public static void main(String[] args) throws IOException {

        final String vcfPath = args[0];
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

        List<String> allSampleNames = new ArrayList<String>(100);
        List<String> caseSampleNames = new ArrayList<String>(100);
        List<String> controlSampleNames = new ArrayList<String>(100);
        boolean doneOnce = false;
        boolean doneOnce2 = false;

        /* =========讀檔(READ FILE)======== */
        final String VcfPath = args[0];
        BufferedReader schemaReader = new BufferedReader(new FileReader(VcfPath));
        VCFCodec vcfCodec = new VCFCodec();
        String line;
        String headerLine = "";
        VariantContext vctx;
        int NFreqHigher = 0;
        while ((line = schemaReader.readLine()) != null) {
            if (line.startsWith("#")) {
                headerLine = headerLine.concat(line).concat("\n");  //先將metadata lines的部分存到headerLine
                continue;
            }
            VCFHeader head = (VCFHeader)vcfCodec.readActualHeader(new LineIteratorImpl(LineReaderUtil.fromStringReader(
                    new StringReader(headerLine), LineReaderUtil.LineReaderOption.SYNCHRONOUS)));



            if (!line.startsWith("#")) {        //開始對data lines(每一筆variants)的操作：
                vctx = vcfCodec.decode(line);
        /* =============================== */



                /* =====操作vctx(play w/ vctx)===== */

                /* 2016/04/07 hw: 取s1~s10當作case, s11~s20當作control計算所有case都有出現但是control都沒有出現的variants數量有多少/
                /*
                用vcfdecoder去幫你把header讀actualheader
                用一個list把 sample ids裝起來
                把list的1..10的名字存起來當成case
                把list的11..20的名字存起來當成control
                ​
                如果(allele的數量是一個){
                    抓出那個allele是誰
                            去算出cases被call出這個allele的數量有多少
                    去算出controls被call出這個allele的數量有多少
                    如果(cases全部都有，control通通沒有) 計數器+1
                }

                如果(allele的數量在兩個以上){
                    迴圈(第一個allele .. 最後一個allele){
                        抓出那個allele是誰
                                去算出cases被call出這個allele的數量有多少
                        去算出controls被call出這個allele的數量有多少
                        如果(cases全部都有，control通通沒有) 計數器+1
                    }
                }
                */

                // 用一個list把 sample ids裝起來，把list的1..10的名字存起來當成case，把list的11..20的名字存起來當成control
                if(!doneOnce) {
                    allSampleNames = vctx.getSampleNamesOrderedByName();
                    for (int i=0;i<10;i++){
                        caseSampleNames.add(allSampleNames.get(i));
                    }
                    for (int i=10;i<20;i++) {
                        controlSampleNames.add(allSampleNames.get(i));
                    }
                    doneOnce=true;
                    continue;
                }

                if(!doneOnce2){
                    System.out.println(allSampleNames.size());
                    System.out.println(caseSampleNames);
                    System.out.println(controlSampleNames);
                    doneOnce2=true;
                    continue;
                }
//                PrintvctxProperties(vctx,head);


                /* 2016/03/17 hw: 找出所有有rsID的Variants */
//                printAllAllelesWithRSID(vctx);

                /*以rsIDList搜尋variants*/
//                String[] TargetRSIDList = {
//                        "rs587638290",
//                        "rs587736341",
//                        "rs534965407",
//                        "rs9609649",
//                        "rs5845047",
//                        "rs80690",
//                        "rs137506",
//                        "rs138355780",};
//                printRSIDEquals(TargetRSIDList,vctx);

                /* 2016/03/24 hw: 找出長度超過100的Allele */
//                printAltAllelesLongerThen(100,vctx);

                /* 2016/03/31 hw1.v2: 用AFComparision()計算chr22 vcf裡面SAS,EAS的allele frequency都高於total的AF的個數有幾個 */
//                if(AFComparison(vctx)) count++;

                /* 2016/03/31 hw2.v2: 找出(算出)八個rsID分別對應的Allele frequency */
//                FindAFsForRSIDs(vctx,TargetRSIDList);

                /* =============================== */



                /* =============================== */
            }


        }


        /* 2016/03/31 hw1.v2: 用AFComparision()計算chr22 vcf裡面SAS,EAS的allele frequency都高於total的AF的個數有幾個 */
//        System.out.println("No. of variants with (SAS_AF>AF) and (EAS_AF>AF):"+count);


        /* 2016/03/31 hw2: 找出(算出)八個rsID分別對應的Allele frequency */
//        System.out.println("\nTask:找出(算出)TargetRSIDList中每個rsID對應的Allele frequency...(please wait)");
//        HashMap<String,Double> rsID_AF = new HashMap<String, Double>();
//        for(int i=0;i<TargetRSIDList.length;i++) {
//            rsID_AF.put(TargetRSIDList[i], -1.0);
//        }
//        System.out.println("Target rsIDs: "+rsID_AF);
//        FindAFsForRSIDs(VcfPath,rsID_AF);
//        System.out.println("AF Results: "+rsID_AF);


        /* 2016/03/31 hw1.v1: 用CompareAllVariantsAFs()計算chr22 vcf裡面SAS,EAS的allele frequency都高於total的AF的個數有幾個 */
//        System.out.println("\nTask:計算 chr22 vcf裡面 SAS, EAS的 allele frequency都高於 total的AF 的個數有幾個...(please wait)");
//        System.out.println("SAS_AF與EAS_AF皆大於AF的Variants個數：" + CompareAllVariantsAFs(vcfPath)); // UNCOMMENT TO RUN!


        System.out.println("Process Finished.");
    }

    /*以sout測試各種VariantContext的methods和members*/
    public static void PrintvctxProperties(VariantContext vctx, VCFHeader head){
        System.out.println(
                " RsID: \t" + vctx.getID() + "\n"+
                        " vctx.toString(): \t" + vctx.toString()+"\n\n"+

                        " -ref: \t" + vctx.getReference() +"\n"+
                        " -alt: \t" + vctx.getAlternateAlleles() +"\n"+
                        " -first alt: \t" + vctx.getAlternateAlleles().get(0) +"\n"+
                        " -first alt length: \t" + vctx.getAlternateAlleles().get(0).length() +"\n"+
                        " -allele Numbers: \t" + vctx.getAlternateAlleles().size()+"\n\n"+

                        " -GT: \t" + vctx.getGenotype(vctx.getSampleNamesOrderedByName().get(0)).getGenotypeString() +"\n"+
                        " -total samples: \t" + vctx.getNSamples() + "\n"+
                        " -total chrms(2x total sample): \t" + vctx.getCalledChrCount() + "\n\n"+

                        "Other properties:\n"+
                        " -getContig(): \t" + vctx.getContig() + "\n"+
                        " -getSource(): \t" + vctx.getSource() + "\n"+
                        " -calcVCFGenotypeKeys(): \t" + vctx.calcVCFGenotypeKeys(head)+"\n"+
                        " -getCommonInfo(): "+vctx.getCommonInfo()+ "\n"

                        +"-----------------------\n"
        );
    }

    /*讀vcf檔，找出SAS_AF與EAS_AF皆大於AF的Variants，並回報符合條件的variants個數*/
    public static int CompareAllVariantsAFs(String vcfPath) throws IOException{

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

    /*回傳vctx中（SAS_AF>AF且SAS_AF>AF）的布林*/
    public static boolean AFComparison(VariantContext vctx){

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
                    return true;
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
                        return true;
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
                    return true;
                }
            }
        }
        return false;
    }

    /*找出TargetRSIDList中每個rsID對應的Allele frequency*/
    public static double FindAFsForRSIDs(VariantContext vctx, String[] TargetRSIDList) {
        int N = TargetRSIDList.length;

        if(TargetRSIDList.length==0){System.out.println("Empty rsIDs target list!");}
        for (int i=0;i<N;i++){
            if (vctx.getID().equals(TargetRSIDList[i])){
                /* 手算Allele frequency卡關中QQ，計算AF先用.getAttribute */
                double AF = vctx.getAttributeAsDouble("AF",-100.0);
                System.out.println("AF of "+TargetRSIDList[i]+": "+AF);
                return AF;
            }
        }
        return 0;
    }

    /*對vctx找出目標rsID(根據TargetRSIDList)對應的Allele frequency*/
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
        int NTotalChr = vctx.getCalledChrCount();   //分母：5008條chrom
        int NAllele = vctx.getAlternateAlleles().size();    //若為1便好處理，若非則須
        int NAlt = 0;   //累加alt的數量（即後面那一堆0|0中的非0有幾個）
        Allele altAllele = vctx.getAlternateAllele(0);
        Set<String> samples = vctx.getSampleNames();
        for (String sample: samples){
            System.out.println(vctx.getGenotype(sample));   //test
            System.out.println(vctx.getGenotype(sample).countAllele(altAllele));    //test
            NAlt+=vctx.getGenotype(sample).countAllele(altAllele);
        }
        return 0.0;
    }

    /*print出長度超過length的variants*/
    public static void printAltAllelesLongerThen(int length, VariantContext vctx){
        if (vctx.getAlternateAlleles().get(0).length() > length) {
            System.out.println(
                    "rsID: " + vctx.getID() +
                            " length: " + vctx.getAlternateAlleles().get(0).length() +
                            " ref: " + vctx.getReference() +
                            " alt:" + vctx.getAlternateAlleles().get(0) +
                            " GT: " + vctx.getGenotype(vctx.getSampleNamesOrderedByName().get(0)).getGenotypeString());
        }
    }

    /*以rsIDList搜尋variants*/
    public static void printRSIDEquals(String[] rsIDList, VariantContext vctx){
        for (int i=0; i<rsIDList.length; i++) {
            if (vctx.getID().equals(rsIDList[i])) {
                System.out.println(
                        "  {rsID: " + vctx.getID()
                                + "} {POS: " + vctx.getEnd()
                                + "} {ref: " + vctx.getReference()
                                + "} {alt(getAlternateAlleles): " + vctx.getAlternateAlleles().get(0)
                                + "} {GT: " + vctx.getGenotype(vctx.getSampleNamesOrderedByName().get(0)).getGenotypeString()
                                + "}");
            }
        }
    }

    /*print出所有擁有rsID的variants*/
    public static void printAllAllelesWithRSID(VariantContext vctx){
        if (!(vctx.getID() == ".")) {
            System.out.println(
                    "|rsID: " + vctx.getID()
                            + "| |ref: " + vctx.getReference()
                            + "| |alt: " + vctx.getAlternateAlleles().get(0)
                            + "| |GT: " + vctx.getGenotype(vctx.getSampleNamesOrderedByName().get(0)).getGenotypeString()
                            + "| |"
            );
        }
    }

    /*[Template] 讀進vcf，檔並對data lines(vctx)進行操作*/
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


