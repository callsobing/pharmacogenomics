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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class VcfReaderJunliang
{
    public static void main( String[] args ) throws IOException {
        long start = System.currentTimeMillis( );
        VCFCodec vcfCodec = new VCFCodec();
        final String vcfPath = args[0];

        BufferedReader schemaReader = new BufferedReader(new FileReader(vcfPath));

        String line;
        String headerLine = "";
        VariantContext vctx;
        int num=50;
        int Count[] = new int [num];
        boolean AlreadyGetID = false;
        String[][] cases = new String[num][5];
        String[][] control = new String[num][5];

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

                boolean isAnswer = true;
                Allele CompareAllele = vctx.getAlternateAlleles().get(0);
                for(int i = 0; i < num; i++)
                    for (int j = 0; j < 5 && isAnswer; j++) {
                        isAnswer=(!(vctx.getGenotype(cases[i][j]).countAllele(CompareAllele)==0))&&(vctx.getGenotype(control[i][j]).countAllele(CompareAllele)==0);
                        if(isAnswer) {
                            Count[i]=Count[i]+1;
                        }
                    }
            }
        }
        for(int i = 0; i < num; i++)
            System.out.println("sampling"+i+":"+Count[i]);
        long end = System.currentTimeMillis( );
        System.out.println("time cost : " + (end - start) +"ms");
    }
}

