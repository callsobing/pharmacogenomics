package C4lab;

import htsjdk.tribble.readers.LineIteratorImpl;
import htsjdk.tribble.readers.LineReaderUtil;
import htsjdk.variant.variantcontext.Allele;
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
        boolean skipGoingThroughVCFinMain = false;


        /* 2016/04/14 hw: 延續上週，用early out 以及 sample name id改用int的list表示，來減少String comparison。目標15分鐘 */
        /* 2016/04/07 hw: 取s1~s10當作case, s11~s20當作control計算所有case都有出現但是control都沒有出現的variants數量有多少(ans==32)*/
//        System.out.println(SamplingWithNRandomSamples(VcfPath,1000,5));
        /* 畫圖（histogram） */
//        List<Integer> testList =new ArrayList<>();
////        int[] a = { 2,2,2,  3,3,3,  4,5,6,7,8, 0,0,0,0,0};
////        int[] a = {11, 52, 20, 66, 33, 26, 37, 79, 177, 29, 13, 10, 303, 22, 22, 56, 20, 12, 13, 32, 38, 13, 45, 35, 51, 14, 11, 45, 24, 6, 12, 23, 93, 26, 18, 8, 15, 14, 17, 7, 21, 30, 7, 50, 17, 8, 59, 14, 16, 55, 35, 1, 37, 28, 24, 22, 77, 24, 18, 16, 32, 47, 18, 10, 67, 19, 58, 62, 38, 9, 26, 10, 16, 16, 28, 75, 35, 18, 58, 26, 35, 43, 13, 14, 11, 80, 202, 36, 134, 14, 24, 12, 29, 20, 88, 69, 20, 12, 30, 12, 162, 45, 17, 17, 22, 25, 16, 18, 27, 27, 85, 43, 19, 16, 51, 16, 124, 5, 68, 17, 18, 16, 133, 10, 34, 21, 18, 11, 31, 8, 31, 35, 13, 22, 14, 7, 111, 68, 20, 20, 138, 38, 59, 25, 23, 73, 67, 42, 0, 32, 2, 50, 28, 74, 21, 17, 24, 18, 102, 104, 23, 15, 127, 65, 6, 25, 124, 23, 33, 59, 93, 14, 43, 26, 76, 4, 12, 13, 65, 48, 12, 20, 13, 40, 22, 36, 52, 19, 5, 73, 8, 18, 18, 73, 17, 25, 5, 49, 3, 8, 0, 8, 43, 13, 34, 46, 17, 60, 13, 23, 16, 139, 35, 64, 10, 58, 8, 11, 10, 32, 13, 8, 22, 15, 14, 24, 4, 78, 116, 36, 53, 4, 35, 49, 10, 35, 10, 32, 65, 65, 35, 30, 17, 16, 7, 80, 8, 104, 17, 11, 15, 45, 23, 53, 29, 11, 22, 0, 27, 51, 27, 31, 16, 107, 116, 57, 118, 20, 67, 58, 63, 112, 26, 48, 6, 183, 20, 25, 27, 38, 11, 15, 27, 68, 21, 11, 9, 14, 9, 23, 29, 66, 40, 11, 14, 33, 15, 39, 94, 20, 18, 31, 10, 35, 62, 8, 13, 14, 28, 6, 235, 53, 12, 38, 12, 43, 10, 23, 15, 14, 64, 179, 587, 17, 32, 32, 10, 25, 22, 166, 110, 27, 39, 56, 26, 7, 57, 1, 9, 15, 36, 30, 22, 7, 22, 27, 28, 103, 43, 18, 13, 37, 101, 4, 12, 5, 40, 20, 35, 26, 15, 225, 14, 17, 25, 35, 95, 327, 66, 4, 68, 8, 25, 44, 53, 30, 14, 22, 40, 19, 30, 41, 30, 69, 55, 19, 35, 22, 14, 7, 2, 11, 120, 42, 29, 31, 22, 8, 6, 9, 15, 24, 34, 23, 101, 9, 37, 29, 142, 62, 4, 13, 21, 35, 0, 120, 38, 29, 7, 16, 68, 9, 50, 7, 11, 3, 24, 23, 13, 10, 46, 4, 41, 31, 9, 21, 13, 28, 10, 15, 41, 101, 84, 104, 5, 67, 25, 32, 53, 8, 18, 66, 64, 226, 5, 68, 6, 5, 12, 35, 46, 46, 27, 47, 14, 7, 114, 11, 17, 35, 7, 19, 35, 37, 55, 4, 60, 18, 29, 29, 28, 10, 30, 26, 8, 57, 13, 13, 101, 10, 25, 32, 24, 13, 41, 0, 10, 59, 72, 17, 17, 30, 16, 9, 16, 55, 21, 35, 17, 0, 30, 2, 21, 39, 223, 11, 4, 22, 0, 63, 23, 6, 26, 43, 24, 22, 56, 39, 35, 23, 70, 28, 51, 3, 52, 106, 6, 1, 38, 29, 27, 30, 4, 10, 23, 39, 19, 21, 11, 19, 61, 43, 3, 63, 4, 10, 49, 13, 9, 15, 9, 23, 18, 25, 106, 26, 27, 15, 28, 45, 10, 13, 47, 33, 23, 3, 7, 4, 126, 10, 27, 51, 31, 5, 25, 79, 12, 8, 3, 13, 58, 34, 72, 22, 9, 3, 72, 78, 4, 92, 46, 14, 73, 10, 10, 11, 8, 27, 6, 77, 79, 7, 28, 8, 27, 60, 26, 30, 50, 39, 53, 72, 10, 39, 99, 45, 37, 20, 43, 4, 10, 31, 12, 22, 27, 17, 36, 20, 15, 22, 32, 55, 13, 32, 61, 22, 17, 76, 26, 10, 134, 21, 40, 112, 32, 67, 44, 5, 24, 70, 28, 13, 30, 27, 22, 46, 27, 52, 6, 85, 71, 22, 7, 86, 21, 121, 11, 45, 54, 31, 8, 32, 35, 19, 42, 5, 9, 43, 13, 29, 14, 34, 26, 7, 20, 38, 69, 8, 158, 10, 32, 44, 12, 51, 42, 18, 15, 34, 10, 159, 46, 20, 11, 54, 29, 54, 13, 12, 30, 74, 47, 19, 49, 15, 248, 20, 28, 90, 54, 8, 33, 27, 51, 7, 100, 103, 46, 10, 28, 5, 18, 51, 86, 166, 8, 23, 7, 11, 4, 62, 11, 63, 16, 70, 25, 15, 10, 4, 75, 25, 0, 22, 8, 40, 10, 48, 9, 17, 29, 60, 7, 12, 33, 61, 13, 41, 14, 22, 20, 3, 12, 19, 24, 0, 60, 10, 10, 19, 37, 23, 4, 8, 21, 12, 28, 34, 22, 26, 13, 62, 17, 7, 29, 15, 17, 14, 6, 11, 26, 8, 18, 27, 42, 20, 206, 9, 18, 25, 20, 10, 0, 41, 345, 17, 4, 20, 18, 10, 83, 18, 8, 23, 20, 45, 20, 27, 34, 79, 13, 26, 73, 146, 22, 12, 35, 23, 13, 29, 32, 27, 262, 47, 67, 16, 72, 25, 33, 17, 17, 33, 21, 11, 11, 12, 85, 21, 30, 33, 44, 10, 21, 9, 80, 59, 19, 23, 22, 32, 34, 8, 13, 81, 13, 11, 48, 170, 174, 17, 70, 21, 11, 45, 85, 72, 19, 17, 25, 8, 91, 14, 69, 157, 49, 35, 88, 43, 11, 46, 11, 29, 39, 19, 18, 20, 29, 16, 2, 9, 16, 54, 54, 159, 44, 27, 18, 8, 25, 11, 50, 16, 16, 16, 29, 28, 23, 199, 21, 23, 25, 17, 71, 46, 12, 30, 8, 6, 4, 4, 78, 18, 48, 18, 9, 10, 17, 11, 76, 98, 9, 29, 32, 4, 0, 96, 196, 27, 26, 24, 10, 43, 33, 1, 33, 13, 41, 79, 5, 10, 35, 0, 3, 0, 45, 22, 40, 21, 23, 68, 4, 170, 57, 14, 21, 52, 16, 39, 18, 14, 24, 5};
//        int[] a = {43, 106, 29, 13, 39, 74, 26, 142, 23, 10, 30, 18, 45, 9, 19, 58, 49, 45, 1, 34, 40, 51, 53, 75, 25, 44, 77, 29, 0, 5, 13, 120, 4, 19, 24, 0, 23, 21, 21, 30, 14, 81, 15, 9, 3, 0, 13, 2, 19, 28, 41, 28, 10, 41, 6, 29, 35, 31, 13, 28, 1, 9, 17, 33, 64, 12, 39, 28, 105, 5, 23, 12, 39, 29, 21, 18, 6, 5, 9, 38, 103, 66, 17, 20, 88, 80, 14, 50, 101, 15, 22, 28, 12, 11, 32, 63, 11, 270, 34, 80, 18, 28, 27, 96, 2, 6, 19, 23, 48, 99, 10, 20, 16, 13, 12, 49, 53, 121, 35, 56, 72, 24, 98, 6, 42, 4, 41, 34, 22, 9, 25, 52, 6, 33, 226, 106, 11, 4, 32, 12, 3, 27, 30, 5, 19, 21, 75, 36, 12, 11, 44, 127, 2, 5, 163, 34, 0, 16, 11, 70, 32, 48, 212, 4, 24, 44, 8, 15, 3, 87, 30, 186, 8, 8, 20, 14, 101, 14, 6, 27, 11, 102, 10, 8, 39, 22, 20, 2, 27, 68, 12, 27, 11, 7, 51, 21, 11, 52, 14, 41, 9, 5, 10, 30, 66, 32, 5, 8, 9, 34, 49, 43, 9, 9, 28, 5, 100, 114, 20, 15, 174, 116, 33, 67, 37, 56, 40, 116, 66, 21, 8, 23, 45, 10, 15, 53, 25, 32, 10, 253, 33, 30, 7, 49, 39, 7, 82, 36, 29, 55, 33, 17, 58, 17, 19, 11, 19, 1, 105, 49, 8, 15, 28, 73, 33, 55, 10, 19, 55, 167, 10, 15, 19, 12, 12, 152, 7, 10, 17, 9, 27, 14, 92, 17, 19, 13, 41, 16, 161, 6, 14, 11, 17, 33, 20, 50, 28, 21, 21, 13, 43, 35, 37, 18, 0, 27, 37, 92, 19, 4, 26, 42, 35, 13, 12, 85, 27, 8, 69, 21, 30, 63, 25, 6, 31, 11, 22, 13, 12, 9, 20, 46, 13, 69, 122, 14, 44, 5, 17, 76, 198, 45, 26, 17, 16, 62, 25, 15, 26, 13, 16, 5, 10, 98, 30, 133, 52, 161, 37, 118, 56, 311, 52, 25, 95, 16, 30, 30, 8, 54, 30, 9, 9, 39, 13, 37, 142, 15, 25, 13, 16, 4, 40, 15, 225, 22, 121, 28, 22, 24, 28, 136, 201, 57, 15, 17, 10, 77, 54, 51, 26, 23, 84, 8, 11, 23, 18, 39, 43, 9, 12, 40, 53, 22, 130, 23, 26, 29, 13, 14, 160, 69, 88, 50, 29, 68, 28, 23, 55, 14, 13, 20, 80, 34, 5, 64, 18, 9, 29, 25, 38, 4, 15, 4, 34, 56, 13, 8, 15, 55, 29, 18, 81, 37, 16, 20, 110, 112, 16, 84, 61, 11, 10, 37, 11, 48, 33, 17, 21, 178, 22, 32, 6, 4, 37, 39, 17, 40, 156, 25, 44, 62, 19, 14, 13, 35, 32, 21, 36, 91, 7, 26, 11, 27, 55, 88, 96, 19, 60, 56, 29, 10, 33, 24, 51, 91, 11, 18, 94, 6, 19, 17, 42, 80, 9, 67, 16, 110, 25, 20, 34, 3, 157, 5, 0, 0, 16, 39, 30, 10, 19, 16, 9, 28, 14, 19, 8, 22, 53, 38, 13, 41, 67, 90, 9, 33, 21, 7, 6, 15, 9, 30, 31, 4, 14, 54, 58, 11, 129, 16, 12, 18, 126, 84, 24, 17, 36, 0, 11, 30, 30, 28, 21, 32, 75, 29, 13, 58, 29, 41, 36, 9, 71, 32, 30, 24, 19, 10, 61, 42, 60, 107, 48, 19, 54, 25, 33, 8, 35, 24, 16, 11, 70, 34, 6, 12, 5, 45, 31, 21, 9, 28, 35, 39, 10, 152, 46, 11, 45, 89, 115, 27, 13, 116, 11, 38, 54, 65, 46, 24, 29, 70, 46, 14, 30, 108, 37, 37, 4, 45, 26, 48, 14, 23, 48, 51, 48, 68, 54, 22, 34, 7, 9, 32, 67, 35, 42, 26, 42, 8, 50, 8, 13, 30, 12, 20, 20, 12, 11, 51, 7, 42, 9, 27, 101, 36, 28, 23, 14, 0, 59, 40, 102, 0, 21, 44, 40, 38, 50, 30, 66, 99, 14, 62, 15, 0, 30, 7, 35, 138, 151, 21, 92, 25, 16, 98, 36, 11, 195, 28, 126, 4, 14, 24, 32, 15, 38, 79, 584, 16, 55, 39, 25, 13, 12, 10, 22, 35, 32, 195, 19, 89, 3, 46, 12, 1, 21, 7, 41, 8, 120, 117, 28, 12, 47, 166, 53, 161, 14, 19, 4, 16, 75, 13, 0, 24, 56, 37, 40, 62, 67, 42, 59, 11, 10, 52, 24, 32, 106, 4, 6, 17, 72, 40, 15, 20, 24, 7, 73, 47, 0, 69, 15, 72, 54, 39, 12, 18, 52, 22, 99, 59, 20, 27, 33, 19, 23, 56, 67, 10, 75, 6, 143, 54, 76, 88, 30, 11, 127, 18, 26, 9, 10, 20, 44, 11, 13, 29, 11, 16, 39, 169, 70, 15, 39, 12, 95, 15, 24, 21, 50, 12, 49, 24, 41, 21, 54, 38, 159, 33, 7, 4, 22, 176, 200, 28, 30, 51, 100, 52, 59, 49, 109, 13, 7, 22, 12, 50, 76, 6, 19, 24, 21, 10, 33, 161, 41, 33, 31, 46, 0, 14, 11, 22, 74, 58, 19, 11, 2, 27, 10, 39, 36, 3, 4, 33, 12, 63, 12, 248, 119, 51, 13, 17, 10, 9, 39, 196, 32, 33, 24, 37, 60, 7, 20, 45, 22, 49, 190, 24, 60, 45, 24, 29, 33, 67, 31, 20, 41, 52, 30, 14, 108, 16, 4, 43, 31, 29, 47, 25, 7, 18, 7, 14, 3, 42, 9, 70, 57, 29, 34, 24, 23, 46, 35, 60, 65, 20, 12, 23, 30, 10, 36, 67, 28, 15, 70, 32, 22, 13, 74, 40, 78, 19, 28, 28, 8, 25, 22, 67, 23, 29, 33, 23, 34, 29, 20, 28, 25, 15, 55, 7, 8, 17, 68, 50, 39, 22, 45, 38, 16, 46, 29, 66, 5, 53, 21, 37, 4, 20};
//        for(int aa:a) testList.add(aa);
//        plotHistogramFromList(testList);


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


                /* 2016/05/26 hw: 手算AF（spark version) */
                    printRSIDandAF(vctx);


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


    /* It takes a list of integer and plot accumulate the elements to plot histogram */
    public static void plotHistogramFromList(List<Integer> list){
        System.out.println("*RESULT: "+list);
        Collections.sort(list);
        System.out.println("*HISTOGRAM: ");

        final int LENGTH = list.size(); //16
        final int MIN = list.get(0);    //0
        final int MAX = list.get(LENGTH-1); //8
        int[] accu = new int[MAX+1]; // int[9] (accu[0] ... accu[8])

        //accumulation
        int j=-1; // j from 0 to 8 (index for accu[8])
        for(int k=MIN;k<LENGTH;k++){ // from 0 to 15

            while (list.get(k)!=j) {
                j++;
                System.out.print("\n"+j+":\t");
            }

            if(list.get(k)==j){
                accu[j]++;
                System.out.print("Ｏ");
            }

        }
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

                        // case sample中如果有allele符合第i個alt，case sample的計數器++ (String comparison)
//                            for (Integer caseNames : caseSampleNames) {
//                                if ((vctx.getGenotype(caseNames).getAllele(0).toString().equals(ithAlt)) ||
//                                        (vctx.getGenotype(caseNames).getAllele(1).toString().equals(ithAlt))) {
//                                    caseCalled++;
//                                }else break;
//                            }

                        for (Integer caseNames : caseSampleNames) {
                            if (vctx.getGenotype(caseNames).countAllele(altAllele)>0) { //TODO countAllele可以移到
                                caseCalled++;
                            }else {
                                earlyOut=true;
                                break;
                            }
                        }

                        if(earlyOut) break;

                        // control sample中如果有allele符合第i個alt，control sample的計數器++
                        for (Integer controlNames : controlSampleNames) {
                            if (vctx.getGenotype(controlNames).countAllele(altAllele)>0) {
                                controlCalled++;
                                earlyOut=true;
                                break;
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
                        rsID_AF.put(key,CalAF(vctx));
                        rsID_AF.put(key,vctx.getAttributeAsDouble("AF",-100.0));

                    }
                }

                /* ============================================= */

            }
        }
        return 1;
    }

    /*)讀vctx檔，計算該筆variant的allele frequency*/
    public static double CalAF(VariantContext vctx){
        double NTotalChr = vctx.getCalledChrCount();   //5008
        int NAllele = vctx.getAlternateAlleles().size();    //若為1便好處理，若非則須
        double altCount = 0;   //累加alt的數量（即後面那一堆0|0中的非0有幾個）
        Allele alt = vctx.getAlternateAllele(0);
        Set<String> samples = vctx.getSampleNames();
        for (String sample: samples){
//            System.out.println(vctx.getGenotype(sample));
            altCount += vctx.getGenotype(sample).countAllele(alt);
        }
//        System.out.println("REAL AF: "+vctx.getAttributeAsDouble("AF",-8888888888888888888.8));
//        System.out.println(altCount/NTotalChr);
        return altCount/NTotalChr;
    }

    public static Pair<String,Float> printRSIDandAF(VariantContext vctx){
        String rsid = vctx.getID();
        Float alleleFreq = 0.0f;
        // TODO:加入你們算AF的邏輯在這裡
        float total = vctx.getCalledChrCount();    //5008
        float count = 0.0f;
        Allele alt = vctx.getAlternateAllele(0);    // [G]
        Set<String> sampleNames = vctx.getSampleNames();
        for(String name: sampleNames){
            count += vctx.getGenotype(name).countAllele(alt);
        }
        alleleFreq = count/total;
        Pair result = new Pair(rsid,alleleFreq);
        System.out.printf("%s has AF of %f\n",result.getRSID(),result.getAF());
        return new Pair(rsid,alleleFreq);
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

    public static class Pair<String,Float> {

        private final String rsid;
        private final Float af;

        public Pair(String rsid, Float af) {
            this.rsid = rsid;
            this.af = af;
        }

        public String getRSID(){return this.rsid;}
        public Float getAF(){return this.af;}

    }

}


