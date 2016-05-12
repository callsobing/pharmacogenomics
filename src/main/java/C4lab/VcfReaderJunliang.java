package C4lab;

import htsjdk.tribble.readers.LineIteratorImpl;
import htsjdk.tribble.readers.LineReaderUtil;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;

import javax.lang.model.type.NullType;
import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

public class VcfReaderJunliang
{
    public static void main( String[] args ) throws IOException {
        long start = System.currentTimeMillis( ); //紀錄起始時間
        VCFCodec vcfCodec = new VCFCodec();
        final String vcfPath = args[0];
        BufferedReader schemaReader = new BufferedReader(new FileReader(vcfPath));
        String line;
        String headerLine = "";
        VariantContext vctx;
        int num = Integer.parseInt (args[1]); //設定取樣次數
        int Count[] = new int [num];
        //boolean AlreadyGetID = false; //紀錄是否已取得sample id
        //String[][] cases = new String[num][5];
        //String[][] control = new String[num][5];

        //隨機取出不重複0~2053的數字 一組10個
        int rnd;
        Integer result[][] = new Integer[num][10];

        for(int i = 0; i < num; i++) {
            HashSet rndSet = new HashSet<Integer>(10);
            for (int j = 0; j < 10; j++) {
                rnd = (int) (2504 * Math.random());
                while (!rndSet.add(rnd))
                    rnd = (int) (2504 * Math.random());
                result[i][j] = rnd;
            }
        }
        //System.out.println("random sussess");

        while ((line = schemaReader.readLine()) != null) {
            if(line.startsWith("#")) {
                headerLine = headerLine.concat(line).concat("\n");
                continue;
            }
            vcfCodec.readActualHeader(new LineIteratorImpl(LineReaderUtil.fromStringReader(
                    new StringReader(headerLine), LineReaderUtil.LineReaderOption.SYNCHRONOUS)));

            if(!line.startsWith("#")) {
                vctx = vcfCodec.decode(line);
/**
                //分配sample id
                if(!AlreadyGetID) {
                    for (int i = 0; i < num; i++)
                        for(int j = 0; j < 10; j++) {
                        if (j < 5)
                            cases[i][j] = vctx.getSampleNamesOrderedByName().get(result[i][j]);
                        else
                            control[i][j - 5] = vctx.getSampleNamesOrderedByName().get(result[i][j]);
                    }
                    AlreadyGetID=true;
                    //System.out.println("get sample id success");
                }
**/
                //比對 符合則次數+1
                Allele CompareAllele = vctx.getAlternateAlleles().get(0);
                boolean hasAlt[] = new boolean [2504];
                for(int i = 0; i < 2504; i++) {
                    if(vctx.getGenotype(i).countAllele(CompareAllele) != 0)
                        hasAlt[i]=true;
                }

                for(int i = 0; i < num; i++) {
                    boolean isAnswer = true;
                    for (int j = 0; j < 5 && isAnswer; j++) {
                        //isAnswer = (!(vctx.getGenotype(cases[i][j]).countAllele(CompareAllele) == 0)) && (vctx.getGenotype(control[i][j]).countAllele(CompareAllele) == 0);
                        isAnswer = (!hasAlt[result[i][j]]) && (hasAlt[result[i][j+5]]);
                    }
                    if (isAnswer) {
                        Count[i]++;
                    }
                }
            }
        }
        //System.out.println("file output.....");
        //輸出到檔案
        FileWriter out = new FileWriter("output"+System.currentTimeMillis( )+".txt");
        for(int i = 0; i < num; i++)
            out.write("sampling" + (i+1) + "\t" + Count[i]+"\n");
        long end = System.currentTimeMillis( );//紀錄結束時間
        System.out.println("time cost : " + (end - start) +"ms");
        out.write("time cost : " + (end - start) +"ms");//輸出花費時間
        out.close();
        //System.out.println("all complete");
    }
}

//問題待解決: 沒考慮多個alternate allele的情形