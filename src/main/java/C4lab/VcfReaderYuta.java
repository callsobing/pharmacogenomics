package C4lab;

import htsjdk.tribble.readers.LineIteratorImpl;
import htsjdk.tribble.readers.LineReaderUtil;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;
import htsjdk.variant.vcf.VCFHeader;

import java.io.*;
import java.util.*;




public class VcfReaderYuta {

    /* main class盡量簡化，以方便之後的使用彈性 */
    public static void main(String[] args) throws IOException {

        long startTimems = System.currentTimeMillis();
        final String VcfPath = args[0];
        boolean skipGoingThroughVCFinMain = true;


        /* 2016/04/14 hw: 延續上週，用early out 以及 sample name id改用int的list表示，來減少String comparison。目標15分鐘 */
        /* 2016/04/07 hw: 取s1~s10當作case, s11~s20當作control計算所有case都有出現但是control都沒有出現的variants數量有多少(ans==32)*/
        System.out.println(SamplingWithNRandomSamples(VcfPath,1000,5));



        /* 2016/04/07 hw: 取s1~s10當作case, s11~s20當作control計算所有case都有出現但是control都沒有出現的variants數量有多少(ans==32)*/
//        SamplingWithFixedSamples(VcfPath);


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



        if(!skipGoingThroughVCFinMain) {
        /* ========= Read in file ======== */
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
                VCFHeader head = (VCFHeader) vcfCodec.readActualHeader(new LineIteratorImpl(LineReaderUtil.fromStringReader(
                        new StringReader(headerLine), LineReaderUtil.LineReaderOption.SYNCHRONOUS)));


                if (!line.startsWith("#")) {        //開始對data lines(每一筆variants)的操作：
                    vctx = vcfCodec.decode(line);
        /* =============================== */
        /* ========= play w/ vctx ======== */




                /* To understand VariantContext by printing everything */
//                PrintvctxProperties(vctx,head);

                /* 2016/03/17 hw: 找出所有有rsID的Variants */
//                printAllAllelesWithRSID(vctx);

                /* 以rsIDList搜尋variants*/
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
        }



        // timer and finishing messange
        long totalms = System.currentTimeMillis()-startTimems;
        int sec = (int) (totalms / 1000) % 60 ;
        int min = (int) ((totalms / (1000*60)) % 60);
        int hr   = (int) ((totalms / (1000*60*60)) % 24);
        System.out.printf("Program Finished. Runtime: %dhr %dmin %dsec (%d ms)\n",hr,min,sec,totalms);
    }





    /* 2016/04/14 hw: 隨機取 5 vs 5 的sample set，重複做200次 */
    public static List<Integer> SamplingWithNRandomSamples(String VcfPath, int NSampling, int sampleSize) throws IOException{


        //TODO avoid redundant variables declaration

        // variables declaration
        final int N = NSampling;
        final int CASESIZE = sampleSize;
        List<String> allSampleNames;
        List<List<Integer>> target = new ArrayList<>();
        List<Integer> report = new ArrayList<>();
        int[] matchCount = new int[N];
        double sumNMatched = 0;
        long startTime = System.currentTimeMillis();
        boolean doneOnce = false;

        List<Integer> caseSampleNames;
        List<Integer> controlSampleNames;
        int caseCalled, controlCalled;
        Allele refAllele;
        Allele altAllele;
        boolean earlyOut;



        /* =========讀檔(READ FILE)======== */
        BufferedReader schemaReader = new BufferedReader(new FileReader(VcfPath));
        VCFCodec vcfCodec = new VCFCodec();
        String line;
        String headerLine = "";
        VariantContext vctx;
        while ((line = schemaReader.readLine()) != null) {
            if (line.startsWith("#")) {
                headerLine = headerLine.concat(line).concat("\n");  //先將metadata lines的部分存到headerLine
                continue;
            }
            VCFHeader head = (VCFHeader) vcfCodec.readActualHeader(new LineIteratorImpl(LineReaderUtil.fromStringReader(
                    new StringReader(headerLine), LineReaderUtil.LineReaderOption.SYNCHRONOUS)));

            /* prepare the N random list of sample names */
            if(!doneOnce){
                allSampleNames = head.getSampleNamesInOrder();
                System.out.println("Target sample names");
                for(int i=0;i<N;i++){
                    //set the ith row of target to a randomly generated list of sample names
                    target.add(i, GetRandomSampleNamesIdxs(allSampleNames,sampleSize));
                    target.get(i).addAll(sampleSize, GetRandomSampleNamesIdxs(allSampleNames,sampleSize));
                    System.out.println("S"+i+": "+target.get(i));
                }
                doneOnce=true;
            }



            if (!line.startsWith("#")) {        //開始對data lines(每一筆variants)的操作：
                vctx = vcfCodec.decode(line);

        /* =============================== */


            /* =====操作vctx(play w/ vctx)===== */

                //fixme early out
//                if(vctx.getNAlleles()==2) {
//
//                    for (int i = 0; i < N; i++) {   // do 1,000 times
//                        List<Integer> caseSampleNames = target.get(i).subList(0, sampleSize); // l[0] to l[4] as "case"
//                        List<Integer> controlSampleNames = target.get(i).subList(sampleSize, sampleSize + CASESIZE); //l[5] to l[9] as "control"
//
//
//                        String ref = vctx.getReference().toString();    //
//                        String onlyAlt = vctx.getAlternateAllele(0).toString();
//                        int caseCalled = 0, controlCalled = 0;
//
//                        // STEP3: 去算出cases被call出這個allele的數量有多少
//                        for (Integer caseSampleName : caseSampleNames) {
//                            if ((vctx.getGenotype(caseSampleName).getAllele(0).toString().equals(onlyAlt)) ||//FIXME
//                                    (vctx.getGenotype(caseSampleName).getAllele(1).toString().equals(onlyAlt))){
//                                caseCalled++;
//                            }else break;
//                        }
//
//                        // STEP4: 去算出controls被call出這個allele的數量有多少
//                        for (Integer controlSampleName : controlSampleNames) {
//                            if ((vctx.getGenotype(controlSampleName).getAllele(0).toString().equals(onlyAlt)) ||//FIXME
//                                    (vctx.getGenotype(controlSampleName).getAllele(1).toString().equals(onlyAlt))){
//                                controlCalled++;
//                                break;
//                            }
//                        }
//
//                        // STEP5: 如果(cases全部都有，control通通沒有) 計數器+1
//                        if ((caseCalled == caseSampleNames.size()) && (controlCalled == 0)) {
//                            System.out.printf("S%d: %s\t matched criteria.(ref:%s, alt:%s)\n", i, vctx.getID(), ref, onlyAlt);
//                            matchCount[i]++;
//                        }
////                        System.out.printf("* Progress: %d/%d\n",i,N);
//                    }
//                }

                // STEP6: 如果(allele的數量在兩個以上，對每個Allels重複STEP3~5的計算
//                else if(vctx.getNAlleles()>2){
                for (int i = 0; i < N; i++) {
                    caseSampleNames = target.get(i).subList(0, sampleSize); // l[0] to l[4] as "case"
                    controlSampleNames = target.get(i).subList(sampleSize, sampleSize + CASESIZE); //l[5] to l[9] as "control

                    for (int j = 0; j < vctx.getNAlleles() - 1; j++) {
                        refAllele = vctx.getReference();
                        altAllele = vctx.getAlternateAllele(j);
                        caseCalled = 0;
                        controlCalled = 0;
                        earlyOut = false;

                        // case sample中如果有allele符合第i個alt，case sample的計數器++
//                            for (Integer caseNames : caseSampleNames) {
//                                if ((vctx.getGenotype(caseNames).getAllele(0).toString().equals(ithAlt)) ||
//                                        (vctx.getGenotype(caseNames).getAllele(1).toString().equals(ithAlt))) {
//                                    caseCalled++;
//                                }else break;
//                            }

                        for (Integer caseNames : caseSampleNames) {
                            if (vctx.getGenotype(caseNames).countAllele(altAllele)>0) {
                                caseCalled++;
                            }else {
                                earlyOut=true;
                            }
                        }

                        if(earlyOut) break;

                        // control sample中如果有allele符合第i個alt，control sample的計數器++
                        for (Integer controlNames : controlSampleNames) {
                            if (vctx.getGenotype(controlNames).countAllele(altAllele)>0) {
                                controlCalled++;
                                earlyOut=true;
                            }
                        }

                        if(earlyOut) break;

                        // 若 case sample的計數器=5 且 control sample的計數器=0，總計數器++
                        if ((caseCalled == caseSampleNames.size()) && (controlCalled == 0)) {
                            System.out.printf("S%d: %s\t matched criteria.(ref:%s, alt:%s) *\n", i+1, vctx.getID(), refAllele.toString(), altAllele.toString());
                            matchCount[i]++;
                        }
                    }
//                    }
                }
            }
        }


        System.out.printf("\n%d sampling results:\n",N);
        for(int i=0;i<N;i++){
            System.out.printf("S%d: %d\n",i,matchCount[i]);
            report.add(matchCount[i]);
            sumNMatched+=matchCount[i];
        }




//        ArrayList<Integer> samplingResult = new ArrayList<Integer>(NSampling);
//        for(int i=0;i<NSampling;i++){
//            samplingResult.add(i,SamplingWithRandomSamples(GetRandomSampleNamesIdxs(AllSampleName,5),GetRandomSampleNamesIdxs(AllSampleName,5)));
//        }
//        System.out.printf("Sampling %d times with exch sample of %d sample",NSampling,sampleSize);


        //timer
        long totalms = System.currentTimeMillis()-startTime;


        System.out.printf("On average %2f cases matched critiria. Runtime: %d ms\n",sumNMatched/N,totalms);
        return report;
    }

    /* 2016/04/07 hw: 取s1~s10當作case, s11~s20當作control計算所有case都有出現但是control都沒有出現的variants數量有多少(ans==32) */
    public static int SamplingWithFixedSamples(String VcfPath) throws IOException{

        // variables declaration
        long startTimems = System.currentTimeMillis();
        List<String> allSampleNames = new ArrayList<String>();
        List<String> caseSampleNames = new ArrayList<String>();
        List<String> controlSampleNames = new ArrayList<String>();
        boolean doneOnce = false;
        boolean doneOnce2 = false;
        int NMatchedConditions=0;

        /* =========讀檔(READ FILE)======== */
        BufferedReader schemaReader = new BufferedReader(new FileReader(VcfPath));
        VCFCodec vcfCodec = new VCFCodec();
        String line;
        String headerLine = "";
        VariantContext vctx;
        while ((line = schemaReader.readLine()) != null) {
            if (line.startsWith("#")) {
                headerLine = headerLine.concat(line).concat("\n");  //先將metadata lines的部分存到headerLine
                continue;
            }
            VCFHeader head = (VCFHeader) vcfCodec.readActualHeader(new LineIteratorImpl(LineReaderUtil.fromStringReader(
                    new StringReader(headerLine), LineReaderUtil.LineReaderOption.SYNCHRONOUS)));


            if (!line.startsWith("#")) {        //開始對data lines(每一筆variants)的操作：
                vctx = vcfCodec.decode(line);
        /* =============================== */


            /* =====操作vctx(play w/ vctx)===== */

                /* 2016/04/07 hw: 取s1~s10當作case, s11~s20當作control計算所有case都有出現但是control都沒有出現的variants數量有多少(ans==32)*/
                // STEP1: 用一個list把 sample ids裝起來，把list的1..10的名字存起來當成case，把list的11..20的名字存起來當成control
                if (!doneOnce) {
                    allSampleNames = vctx.getSampleNamesOrderedByName();
                    for (int i = 0; i < 5; i++) {
                        caseSampleNames.add(allSampleNames.get(i));
                    }
                    for (int i = 5; i < 10; i++) {
                        controlSampleNames.add(allSampleNames.get(i));
                    }
                    doneOnce = true;
                }

                // [測試用] 輸出各種sample names
//                if(!doneOnce2){
//                    System.out.println(allSampleNames.size());
//                    System.out.println(caseSampleNames);
//                    System.out.println(controlSampleNames);
//                    doneOnce2=true;
//                }
//                PrintvctxProperties(vctx,head);


                // STEP2: 如果allele的數量是一個，抓出那個allele是誰
                if(vctx.getNAlleles()==2) {
                    String ref = vctx.getReference().toString();
                    String onlyAlt = vctx.getAlternateAllele(0).toString();
                    int caseCalled=0, controlCalled=0;

                    // STEP3: 去算出cases被call出這個allele的數量有多少
                    for (String caseSampleName:caseSampleNames){
                        if((vctx.getGenotype(caseSampleName).getAllele(0).toString().equals(onlyAlt))||
                                (vctx.getGenotype(caseSampleName).getAllele(1).toString().equals(onlyAlt))) caseCalled++;
                    }

                    // STEP4: 去算出controls被call出這個allele的數量有多少
                    for (String controlSampleName:controlSampleNames){
                        if((vctx.getGenotype(controlSampleName).getAllele(0).toString().equals(onlyAlt))||
                                (vctx.getGenotype(controlSampleName).getAllele(1).toString().equals(onlyAlt))) controlCalled++;
                    }

                    // STEP5: 如果(cases全部都有，control通通沒有) 計數器+1
                    if((caseCalled==caseSampleNames.size())&&(controlCalled==0)){
                        System.out.printf("%s matched search condition.(ref:%s, alt:%s)\n",vctx.getID(),ref,onlyAlt);
                        NMatchedConditions++;
                    }
                }

                // STEP6: 如果(allele的數量在兩個以上，對每個Allels重複STEP3~5的計算
                else if(vctx.getNAlleles()>2){
                    for (int i=0;i<vctx.getNAlleles()-1;i++){
                        String ref = vctx.getReference().toString();
                        String ithAlt = vctx.getAlternateAllele(i).toString();
                        int caseCalled=0, controlCalled=0;

                        // case sample中如果有allele符合第i個alt，case sample的計數器++
                        for(String caseNames:caseSampleNames) {
                            if((vctx.getGenotype(caseNames).getAllele(0).toString().equals(ithAlt))||
                                    (vctx.getGenotype(caseNames).getAllele(1).toString().equals(ithAlt))) caseCalled++;
                        }

                        // control sample中如果有allele符合第i個alt，control sample的計數器++
                        for(String controlNames:controlSampleNames){
                            if((vctx.getGenotype(controlNames).getAllele(0).toString().equals(ithAlt))||
                                    (vctx.getGenotype(controlNames).getAllele(1).toString().equals(ithAlt))) controlCalled++;
                        }

                        // 若 case sample的計數器=5 且 control sample的計數器=0，總計數器++
                        if((caseCalled==caseSampleNames.size())&&(controlCalled==0)){
                            System.out.printf("%s matched search condition.(ref:%s, alt:%s)\n",vctx.getID(),ref,ithAlt);
                            NMatchedConditions++;
                        }
                    }
                }
            }
        }
        //timer
        long totalms = System.currentTimeMillis()-startTimems;
        System.out.printf("%d cases matched critiria. Runtime: %d ms\n",NMatchedConditions,totalms);
        return 0;
    }

    /* 2016/04/14 hw: Randomly generate a List of N sample names. (use .tailSet() and .headSet() to get 2 sets for "case" and "control" */
    public static List<Integer> GetRandomSampleNamesIdxs(List<String> AllSampleNames, int N){

        int NSample = AllSampleNames.size()-1;  // 2504 samples in total
        Random rng = new Random();
        HashSet<Integer> generated = new HashSet<Integer>();
        while (generated.size()<N){
            Integer next = rng.nextInt(NSample)+1;
            generated.add(next);
//            generated.add(AllSampleNames.get(next));
        }
        List<Integer> l = new ArrayList<Integer>(generated);
        System.out.println("* Generated a List of N sample names: "+l);
        return l;
    }

    /*以sout測試各種VariantContext的methods和members*/
    public static void PrintvctxProperties(VariantContext vctx, VCFHeader head){
        System.out.println("-----------------------\n"+

                        " RsID: \t" + vctx.getID() + "\n"+  // rs62224611
                        " vctx.toString(): \t" + vctx.toString()+"\n\n"+
// [VC Unknown @ 22:121210000 Q100.00 of type=SNP alleles=[A*, C, G] attr={AA=.|||, AC=[478, 17], AF=[0.0954473, 0.00339457],
// AFR_AF=[0.003, 0], AMR_AF=[0.1239, 0], AN=5008, DP=22548, EAS_AF=[0.0744, 0], EUR_AF=[0.0746, 0.003],
// MULTI_ALLELIC=true, NS=2504, SAS_AF=[0.2434, 0.0143], VT=SNP}
// GT=GT	1|0	2|0	0|1	0|2	0|1	0|0	0|0	0|0	0|0	0|0	0|0	1|0	0|0	0|0	0|0	0|0	0|0	0|0	0|0	1|0	0|0	0|1	0|0	0|0	0|0	0|0	0|0
// 0|0	0|1	0|0	0|0	0|0	0|0	0|0	0|0	0|0	0|0	0|0	0|0	0|0	0|0	0|0	0|0	0|0	0|0	0|0	0|0	0|0	0|0	0|0	0|0	0|0	0|0	0|0	0|0	0|0
// 0|0	0|0	0|0	0|0	0|0	0|0	0|0	0|0	0|0	0|0	0|0	0|1	0|0	0|0	0|0	0|0	0|0	0|0	0|0	0|0	0|0	0|0	0|0	1|0	0|0	1|0	0 ...

                        " -ref: \t" + vctx.getReference() +"\n"+                                        // A*
                        " -alt: \t" + vctx.getAlternateAlleles() +"\n"+                                 // [C, G]
                        " -first alt: \t" + vctx.getAlternateAlleles().get(0) +"\n"+                    // C
                        " -first alt length: \t" + vctx.getAlternateAlleles().get(0).length() +"\n"+    // 1
                        " -allele Numbers: \t" + vctx.getAlternateAlleles().size() +"\n"+               // 2
                        " -allele Numbers': \t" + vctx.getNAlleles()+"\n\n"+                            // 3 (1 ref + 2 alt)

                        " -GT(*|*): \t" + vctx.getGenotype(vctx.getSampleNamesOrderedByName().get(0)) +"\n"+                     // [HG00096 C|A*]
                        " -GT(*|*): \t" + vctx.getGenotype(vctx.getSampleNamesOrderedByName().get(0)).getGenotypeString() +"\n"+ // C|A
                        " -GT(*|*): \t" + vctx.getGenotype(vctx.getSampleNamesOrderedByName().get(0)).getAllele(0) +"\n"+        // C
                        " -GT(*|*): \t" + vctx.getGenotype(vctx.getSampleNamesOrderedByName().get(0)).getAllele(1) +"\n"+        // A*
                        " -GT(*|*): \t" + vctx.getGenotype(vctx.getSampleNamesOrderedByName().get(1)) +"\n"+                     // [HG00097 G|A*]
                        " -GT(*|*): \t" + vctx.getGenotype(vctx.getSampleNamesOrderedByName().get(0)).getGenotypeString() +"\n"+ // C|A
                        " -GT(*|*): \t" + vctx.getGenotype(vctx.getSampleNamesOrderedByName().get(1)).getAllele(0) +"\n"+        // G
                        " -GT(*|*): \t" + vctx.getGenotype(vctx.getSampleNamesOrderedByName().get(1)).getAllele(1) +"\n\n"+      // A*

                        " -total samples: \t" + vctx.getNSamples() + "\n"+                              // 2504
                        " -total chrms(2x total sample): \t" + vctx.getCalledChrCount() + "\n\n"+       // 5008

                        "Other properties:\n"+
                        " -getContig(): \t" + vctx.getContig() + "\n"+                          // 22
                        " -getSource(): \t" + vctx.getSource() + "\n"+                          // Unknown
                        " -calcVCFGenotypeKeys(): \t" + vctx.calcVCFGenotypeKeys(head)+"\n"+    // [GT]

                        "-----------------------\n"
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


