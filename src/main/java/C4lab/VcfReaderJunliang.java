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
        int num=200; //設定取樣次數
        int Count[] = new int [num];
        boolean AlreadyGetID = false; //紀錄是否已取得sample id
        String[][] cases = new String[num][5];
        String[][] control = new String[num][5];

        //隨機
        int rnd;
        Integer result[][] = new Integer[num][10];
        HashSet rndSet = new HashSet<Integer>(10);
        for(int i = 0; i < num; i++)
            for(int j = 0; j < 10; j++) {
                rnd=(int)(2500*Math.random());
                while(!rndSet.add(rnd))
                    rnd=(int)(2500*Math.random());
                result[i][j]=rnd;
            }

        while ((line = schemaReader.readLine()) != null) {
            if(line.startsWith("#")) {
                headerLine = headerLine.concat(line).concat("\n");
                continue;
            }
            vcfCodec.readActualHeader(new LineIteratorImpl(LineReaderUtil.fromStringReader(
                    new StringReader(headerLine), LineReaderUtil.LineReaderOption.SYNCHRONOUS)));

            if(!line.startsWith("#")) {
                vctx = vcfCodec.decode(line);

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
                }

                //比對 符合則次數+1
                Allele CompareAllele = vctx.getAlternateAlleles().get(0);
                for(int i = 0; i < num; i++) {
                    boolean isAnswer = true;
                    for (int j = 0; j < 5 && isAnswer; j++) {
                        isAnswer = (!(vctx.getGenotype(cases[i][j]).countAllele(CompareAllele) == 0)) && (vctx.getGenotype(control[i][j]).countAllele(CompareAllele) == 0);
                    }
                    if (isAnswer) {
                        Count[i] = Count[i] + 1;
                    }
                }
            }
        }

        //輸出到檔案
        FileWriter out = new FileWriter("output.txt");
        for(int i = 0; i < num; i++) {
            System.out.println("sampling" + i + ":" + Count[i]);
            out.write("sampling" + i + ":" + (Count[i]+1)+"\n");
        }
        long end = System.currentTimeMillis( );//紀錄結束時間
        System.out.println("time cost : " + (end - start) +"ms");
        out.write("time cost : " + (end - start) +"ms");//輸出花費時間
        out.close();
    }
}

//問題待解決 1.隨機0~2053  2.沒考慮多個alternate allele的情形