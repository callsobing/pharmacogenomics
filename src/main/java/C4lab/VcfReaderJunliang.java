package C4lab;

import htsjdk.tribble.readers.LineIteratorImpl;
import htsjdk.tribble.readers.LineReaderUtil;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;
import org.apache.commons.lang.ArrayUtils;

import javax.lang.model.type.NullType;
import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class VcfReaderJunliang
{
    public static void main( String[] args ) throws IOException {
        long start = System.currentTimeMillis( ); //紀錄起始時間
        VCFCodec vcfCodec = new VCFCodec();
        final String vcfPath = args[0];
        InputStream fileStream = new FileInputStream(vcfPath);
        InputStream gzipStream = new GZIPInputStream(fileStream);
        Reader decoder = new InputStreamReader(gzipStream, "UTF-8");
        BufferedReader schemaReader = new BufferedReader(decoder);
        //BufferedReader schemaReader = new BufferedReader(new FileReader(vcfPath));
        String line;
        String headerLine = "";
        VariantContext vctx;


        //int num = Integer.parseInt (args[1]); //設定取樣次數
        //int Count[] = new int [num];
        //boolean AlreadyGetID = false; //紀錄是否已取得sample id
        //String[][] cases = new String[num][5];
        //String[][] control = new String[num][5];

        //隨機取出不重複0~2053的數字 一組10個
        //int rnd;
        //Integer result[][] = new Integer[num][10];

        //for(int i = 0; i < num; i++) {
        //    HashSet rndSet = new HashSet<Integer>(10);
        //    for (int j = 0; j < 10; j++) {
        //        rnd = (int) (2504 * Math.random());
        //        while (!rndSet.add(rnd))
        //            rnd = (int) (2504 * Math.random());
        //        result[i][j] = rnd;
        //    }
        //}
        //System.out.println("random sussess");

        ArrayList<String> rsid = new ArrayList<>(Arrays.asList("rs3883917","rs368216254","rs1057910","rs113993960",
                "rs121908755","rs121908757","rs121909005","rs121909041","rs12248560", "rs12979860","rs1799853",
                "rs193922525","rs267606723","rs28399504","rs3892097","rs3918290","rs4149056","rs4244285", "rs4986893",
                "rs55886062","rs67376798","rs74503330","rs75527207","rs776746","rs80282562","rs8175347","rs887829","rs9923231"));
        int no_varient = 0 ;
        int one_varient = 0 ;
        int two_varient = 0 ;
        FileWriter out = new FileWriter("output"+System.currentTimeMillis( )+".txt");
        while ((line = schemaReader.readLine()) != null) {
            if(line.startsWith("#")) {
                headerLine = headerLine.concat(line).concat("\n");
                continue;
            }
            vcfCodec.readActualHeader(new LineIteratorImpl(LineReaderUtil.fromStringReader(
                    new StringReader(headerLine), LineReaderUtil.LineReaderOption.SYNCHRONOUS)));

            if(!line.startsWith("#")) {
                vctx = vcfCodec.decode(line);
                int samplesize = vctx.getSampleNames().size();
                out.write("\n");
                if(vctx.getID()==".")
                    continue;
                for(int i = 0; i < rsid.size(); i++) {
                    if (vctx.getID().equals(rsid.get(i))) {
                        Allele CompareAllele = vctx.getAlternateAlleles().get(0);
                        for (int j = 0; j < samplesize; j++) {
                            if (vctx.getGenotype(j).countAllele(CompareAllele) == 0)
                                no_varient++;
                            else if (vctx.getGenotype(j).countAllele(CompareAllele) == 1)
                                one_varient++;
                            else
                                two_varient++;
                        }
                        System.out.println("rsid:"+rsid.get(i)+"\t"+no_varient+"\t"+one_varient+"\t"+two_varient);
                        out.write("rsid:"+rsid.get(i)+"\t"+no_varient+"\t"+one_varient+"\t"+two_varient);
                        rsid.remove(i);
                        no_varient = 0 ;
                        one_varient = 0 ;
                        two_varient = 0 ;
                        System.out.println("remain:"+rsid.size());
                        long now = System.currentTimeMillis();
                        System.out.println("time cost : " + (now - start) +"ms");
                    }
                }
                //比對 符合則次數+1
                //Allele CompareAllele = vctx.getAlternateAlleles().get(0);
                //boolean hasAlt[] = new boolean [2504];
                //for(int i = 0; i < 2504; i++) {
                //    if(vctx.getGenotype(i).countAllele(CompareAllele) != 0)
                //        hasAlt[i]=true;
                //}

                //for(int i = 0; i < num; i++) {
                //    boolean isAnswer = true;
                //    for (int j = 0; j < 5 && isAnswer; j++) {
                //        //isAnswer = (!(vctx.getGenotype(cases[i][j]).countAllele(CompareAllele) == 0)) && (vctx.getGenotype(control[i][j]).countAllele(CompareAllele) == 0);
                //        isAnswer = (!hasAlt[result[i][j]]) && (hasAlt[result[i][j+5]]);
                //    }
                //    if (isAnswer) {
                //       Count[i]++;
                //    }
                //}
            }
        }
        //System.out.println("file output.....");
        //輸出到檔案
        //FileWriter out = new FileWriter("output"+System.currentTimeMillis( )+".txt");
        //for(int i = 0; i < num; i++)
        //    out.write("sampling" + (i+1) + "\t" + Count[i]+"\n");
        long end = System.currentTimeMillis( );//紀錄結束時間
        System.out.println("time cost : " + (end - start) +"ms");
        //out.write("time cost : " + (end - start) +"ms");//輸出花費時間
        out.close();
        //System.out.println("all complete");
    }
}

