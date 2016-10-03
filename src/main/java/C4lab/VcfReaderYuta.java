package C4lab;
import com.sun.org.apache.bcel.internal.generic.POP;
import htsjdk.tribble.readers.LineIteratorImpl;
import htsjdk.tribble.readers.LineReaderUtil;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;
import htsjdk.variant.vcf.VCFHeader;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.io.*;
import java.util.*;
import java.util.Dictionary;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javafx.print.Collation;
import org.apache.commons.collections.iterators.CollatingIterator;
import org.apache.commons.lang3.EnumUtils;
import sun.jvm.hotspot.memory.*;

import javax.print.attribute.standard.MediaSize;

public class VcfReaderYuta {

    private static final String FILETYPE = "gz";
    private static final Integer POPNUM = 26;
    private static final Integer[] popIndex = {10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 22, 22, 22, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 22, 22, 22, 22, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 22, 22, 22, 22, 22, 22, 22, 22, 22, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 10, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 7, 7, 7, 20, 20, 20, 20, 20, 20, 21, 21, 21, 21, 16, 16, 16, 16, 16, 16, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 10, 10, 10, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 0, 0, 0, 0, 0, 0, 0, 0, 20, 20, 0, 0, 0, 0, 0, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 0, 0, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 0, 0, 0, 0, 0, 20, 20, 20, 20, 20, 20, 20, 0, 0, 0, 0, 0, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 0, 0, 0, 0, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20, 20, 0, 20, 20, 20, 0, 0, 0, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 0, 0, 20, 20, 20, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 10, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 3, 20, 20, 0, 0, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 0, 0, 0, 0, 20, 20, 20, 20, 20, 20, 20, 20, 0, 0, 0, 20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20, 20, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 0, 0, 20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 12, 12, 12, 12, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 21, 21, 21, 0, 0, 0, 0, 0, 0, 0, 16, 16, 16, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 12, 12, 12, 12, 12, 12, 12, 0, 0, 12, 12, 12, 12, 12, 12, 12, 12, 21, 21, 21, 21, 21, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 12, 12, 12, 12, 12, 12, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 12, 12, 12, 12, 12, 12, 21, 21, 21, 21, 21, 21, 21, 21, 21, 12, 12, 12, 12, 12, 12, 12, 12, 12, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 12, 12, 2, 2, 2, 2, 21, 21, 21, 21, 21, 21, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 18, 18, 18, 18, 21, 21, 21, 21, 21, 21, 12, 12, 12, 12, 12, 12, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 21, 21, 21, 8, 8, 8, 8, 8, 8, 8, 8, 12, 12, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 21, 21, 21, 21, 21, 21, 21, 21, 23, 23, 23, 23, 23, 21, 21, 21, 21, 21, 21, 21, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 21, 21, 21, 21, 21, 21, 23, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 23, 23, 23, 23, 23, 14, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 21, 21, 21, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 23, 23, 23, 23, 23, 23, 23, 23, 23, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 23, 23, 23, 23, 23, 23, 23, 23, 14, 14, 23, 23, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 23, 23, 23, 23, 23, 23, 23, 14, 14, 14, 14, 14, 14, 14, 14, 23, 14, 14, 14, 14, 14, 14, 14, 23, 23, 23, 23, 14, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 14, 14, 14, 14, 14, 23, 14, 14, 14, 14, 14, 14, 14, 23, 23, 14, 14, 14, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 1, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 1, 1, 1, 1, 1, 1, 1, 1, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11};

    public static void main(String[] args) throws IOException {
        long startTimems = System.currentTimeMillis();

        // rsIDs with Level of Evidence 2A, obtain with:
        // $ awk '$2=="2A" {printf "\"%s\",", $1}' SCV.withrs.tsv
        List<String> rsID2Alist = Arrays.asList(

                // rsIDs with Level of Evidence 2A, obtain with:
                // $ awk '$2=="2A" {printf "%s,", $1}' SCV.withrs.tsv
//                chr1
                "rs1801131","rs1801133","rs2297595","rs6025",
//                chr2
                "rs264631","rs264651","rs4148323","rs8175347",
//                chr3
//                chr4
                "rs145489027",
//                chr5
                "rs1042713","rs17244841",
//                chr6
//                chr7
                "rs1045642","rs113993959","rs121434568","rs121434569","rs2032582","rs2740574","rs75039782","rs77010898", "rs776746",
//                chr8
//                chr9
//                chr10
                "rs1057910","rs12248560","rs28371686","rs4244285","rs4917639","rs56165452","rs7900194",
//                chr11
                "rs1695","rs1799978",
//                chr12
                "rs4149015","rs4149056",
//                chr13
//                chr14
//                chr15
//                chr16
                "rs17708472","rs1800566","rs2359612","rs2884737","rs61742245","rs7294","rs8050894","rs9923231","rs9934438",
//                chr17
                "rs1799752",
//                chr18
//                chr19
                "rs2108622","rs2279343","rs2279345","rs28399499","rs3745274","rs7412",
//                chr20
//                chr21
//                chr22
                "rs3892097","rs4680"
//                chrX

//                chrY
//                chrMT
                ,"rs587755077"  //TODO remove tester:  this is just the first SNP of ALL.chr22.*.vcf
                ,"rs3883917"    //TODO remove tester:  rs3883917       C       T      ... 1/1*2 ...
                ,"rs371543232"  //TODO remove tester:  rs371543232     A       G      ... 0/1*1 ... 1/1*11 ...
                ,"rs370482130"  //TODO remove tester:  rs370482130     T       C/A    ... 0/1*3 ... 2/2*3 ... 1/1*

        );

        String[] popNamesArr = {
                "ACB",  // 0
                "ASW",  // 1
                "BEB",  // 2
                "CDX",
                "CEU",
                "CHB",
                "CHS",
                "CLM",
                "ESN",
                "FIN",
                "GBR",
                "GIH",
                "GWD",
                "IBS",
                "ITU",
                "JPT",
                "KHV",
                "LWK",
                "MSL",
                "MXL",
                "PEL",
                "PJL",
                "PUR",
                "STU",
                "TSI",
                "YRI",  //25
        };

/* 2016/03/31 hw2: 找出(算出)八個rsID分別對應的Allele frequency */
//        System.out.println("\nTask:找出(算出)TargetRSIDList中每個rsID對應的Allele frequency...(please wait)");
//        HashMap<String,Double> rsID_AF = new HashMap<String, Double>();
//        for(int i=0;i<TargetRSIDList.length;i++) {
//            rsID_AF.put(TargetRSIDList[i], -1.0);
//        }
//        System.out.println("Target rsIDs: "+rsID_AF);
//        FindAFsForRSIDs(VcfPath,rsID_AF);
//        System.out.println("AF Results: "+rsID_AF);


        //


        final String VcfPath = args[0];

        BufferedReader schemaReader = new BufferedReader(new FileReader(VcfPath));
        if(FILETYPE=="gz") {
            GZIPInputStream in = new GZIPInputStream(new FileInputStream(VcfPath));
            schemaReader = new BufferedReader(new InputStreamReader(in));
        }
        VCFCodec vcfCodec = new VCFCodec();
        String line;
        String headerLine = "";
        VariantContext vctx;
        System.out.println("rsID\tpop\tfreq(1/1)\tfreq(0/1)\tfreq(0/0)");
            while ((line = schemaReader.readLine()) != null) {
                if (line.startsWith("#")) {
                    headerLine = headerLine.concat(line).concat("\n");
                    continue;
                }
                VCFHeader head = (VCFHeader) vcfCodec.readActualHeader(new LineIteratorImpl(LineReaderUtil.fromStringReader(
                        new StringReader(headerLine), LineReaderUtil.LineReaderOption.SYNCHRONOUS)));


                if (!line.startsWith("#")) {
                    vctx = vcfCodec.decode(line);

                    /* STEP 1: skip those without rsID */
                    if(vctx.emptyID()) continue;

//                    /* STEP 2 (version1): if rsID matches, do STEP 3 */
//                    if(EnumUtils.isValidEnum(rsID.class,vctx.getID())){
//                        System.out.print(vctx.getID());
//
//                        /* STEP 3: calculate phenotype frequencies for this vctx */
//                        for(Double[] freqs : getPhenotypeFreqs(vctx)){
//                            System.out.println("\t"+freqs[0]+ "\t"+freqs[1]+"\t"+freqs[2]);
//                        }
//
//                    }

                    /* STEP 2 (version 2) */
                    if(rsID2Alist.contains(vctx.getID())) {

                        if (POPNUM!=1) {

                            String rsid = vctx.getID();

                            List<Double[]> result = getPhenotypeFreqsMultiPop(vctx);
                            for(int i=0;i<POPNUM;i++){
                                String pop = popNamesArr[i];
                                Double[] freqs = result.get(i);
                                System.out.printf("%s\t%s\t%f\t%f\t%f\n",rsid,pop,freqs[0],freqs[1],freqs[2]);
                            }

                        }else{
                        /* STEP 3: calculate phenotype frequencies for this vctx */
                            for (Double[] freqs : getPhenotypeFreqs(vctx)) {
                                System.out.print(vctx.getID());
                                System.out.println("\t" + freqs[0] + "\t" + freqs[1] + "\t" + freqs[2]);
                            }
                        }
                    }

                /* 2016/05/26 hw: 手算AF（spark version) */
//
//                    if(vctx.getAlternateAlleles().size()==1){
//                        rsID_AF(vctx, true);
//                    }
//                    else {
//                        int NAlt = vctx.getAlternateAlleles().size();   // usually 2 or 3
//                        String rsid = vctx.getID();
//
//                        for(int i=0;i<NAlt;i++) {
//                            Double alleleFreq = ((Double) ((ArrayList) vctx.getAttribute("AF")).get(i));
//                        }
//                    }

                }
            }

        long totalms = System.currentTimeMillis()-startTimems;
        int sec = (int) (totalms / 1000) % 60 ;
        int min = (int) ((totalms / (1000*60)) % 60);
        int hr   = (int) ((totalms / (1000*60*60)) % 24);
        System.out.printf("Finished. Runtime: %dhr %dmin %dsec (%d ms)\n",hr,min,sec,totalms);
    }


    /* vctx -> phenotype frequencies:{freq(1/1), freq(1/0||0/1), freq(0/0||./.)} */
    public static List<Double[]> getPhenotypeFreqs(VariantContext vctx){

        List<Double[]> list = new LinkedList<>();

        //single variant
        if(vctx.getNAlleles()==2) {

            int totalChr = vctx.getNSamples();  //496
            int homCount = vctx.getHomVarCount();
            int hetCount = vctx.getHetCount();

            // counting by hand
//        int homCount = 0, hetCount = 0;
//          for(Genotype gt:vctx.getGenotypesOrderedByName()) {
//               if(gt.isCalled()){
//
//                   if(gt.isHom()) homCount++;
//                   if(gt.isHet()) hetCount++;
//
//               }else continue;
//           }

            list.add(new Double[]{homCount / (double) totalChr, hetCount / (double) totalChr, ((totalChr - homCount) - hetCount) / (double) totalChr});
            return list;



        //multi variants
        }else {

            for(int i=0;i<vctx.getNAlleles()-1;i++){    // i = 0,1...2

                int totalChr = vctx.getNSamples();  //496
                Double homCount = 0.0;
                Double hetCount = 0.0;

                Allele altAllele = vctx.getAlternateAllele(i);


                for(int j=0;j<vctx.getNSamples();j++){
                    int altCount = vctx.getGenotype(j).countAllele(altAllele);
                    if (altCount==0){
                        continue;
                    }else if (altCount==1){
                        hetCount++;
                    }else if (altCount==2){
                        homCount++;
                    }else {
                        System.out.println("WTF!?!?!?");
                    }
                }

                list.add(new Double[]{homCount/totalChr, hetCount/totalChr, ((totalChr-homCount)-hetCount)/totalChr});

            }
            return list;

        }

    }

    public static List<Double[]> getPhenotypeFreqsMultiPop(VariantContext vctx){

        int NSamples = vctx.getNSamples();  //2504 (t1g)

        Double[] defaultFreqs = {0.0, 0.0, 0.0};
        ArrayList<Double[]> result = new ArrayList<>(Collections.nCopies(POPNUM, defaultFreqs));

        for(int i=0;i<vctx.getNAlleles()-1;i++){    // i = 0,1...2

            ArrayList<Double> homCountList = new ArrayList<>(Collections.nCopies(POPNUM, 0.0));
            ArrayList<Double> hetCountList = new ArrayList<>(Collections.nCopies(POPNUM, 0.0));

            Allele altAllele = vctx.getAlternateAllele(i);

            for(int j=0;j<vctx.getNSamples();j++){
                int altCount = vctx.getGenotype(j).countAllele(altAllele);
                if (altCount==0){
                    continue;
                }else if (altCount==1){

                    Double temp = hetCountList.get(popIndex[j]);
                    hetCountList.set(popIndex[j],temp+1.0);

                }else if (altCount==2){

                    Double temp = homCountList.get(popIndex[j]);
                    homCountList.set(popIndex[j],temp+1.0);

                }
            }

            for(int k=0;k<POPNUM;k++){
                Double[] freqs = {
                        homCountList.get(k)/NSamples,
                        hetCountList.get(k)/NSamples,
                        1-(homCountList.get(k)/NSamples+hetCountList.get(k)/NSamples)
                };
                result.set(k,freqs);
            }

        }

        return result;
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
        List<String> allSampleNames;
        List<List<Integer>> target = new ArrayList<>();
        List<Integer> report = new ArrayList<>();
        int[] matchCount = new int[NSampling];
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
                for(int i = 0; i< NSampling; i++){
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
                for (int i = 0; i < NSampling; i++) {
                    caseSampleNames = target.get(i).subList(0, sampleSize); // l[0] to l[4] as "case"
                    controlSampleNames = target.get(i).subList(sampleSize, sampleSize + sampleSize); //l[5] to l[9] as "control

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


        System.out.printf("\n%d sampling results:\n", NSampling);
        for(int i = 0; i< NSampling; i++){
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


        System.out.printf("On average %2f cases matched critiria. Runtime: %d ms\n",sumNMatched/ NSampling,totalms);
        return report;
    }

    /* 2016/04/07 hw: 取s1~s10當作case, s11~s20當作control計算所有case都有出現但是control都沒有出現的variants數量有多少(ans==32) */
    public static int SamplingWithFixedSamples(String VcfPath) throws IOException{

        // variables declaration
        long startTimems = System.currentTimeMillis();
        List<String> allSampleNames = new ArrayList<>();
        List<String> caseSampleNames = new ArrayList<>();
        List<String> controlSampleNames = new ArrayList<>();
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
        HashSet<Integer> generated = new HashSet<>();
        while (generated.size()<N){
            Integer next = rng.nextInt(NSample)+1;
            generated.add(next);
//            generated.add(AllSampleNames.get(next));
        }
        List<Integer> l = new ArrayList<>(generated);
        System.out.println("* Generated a List of N sample names: "+l);
        return l;
    }

    /*以sout測試各種VariantContext的methods和members*/
    public static void PrintVctxProperties(VariantContext vctx){
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
                        " " +vctx +

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
//                        " -calcVCFGenotypeKeys(): \t" + vctx.calcVCFGenotypeKeys(head)+"\n"+    // [GT]

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
                        ArrayList<String> AFlist = (ArrayList<String>) vctx.getAttribute("AF", "EMPTY");
                        ArrayList<String> SAS_AFlist = (ArrayList<String>) vctx.getAttribute("SAS_AF", "EMPTY");
                        ArrayList<String> EAS_AFlist = (ArrayList<String>) vctx.getAttribute("EAS_AF", "EMPTY");
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
                    ArrayList<String> AFlist = (ArrayList<String>)vctx.getAttribute("AF","EMPTY");
                    ArrayList<String> SAS_AFlist = (ArrayList<String>)vctx.getAttribute("SAS_AF","EMPTY");
                    ArrayList<String> EAS_AFlist = (ArrayList<String>)vctx.getAttribute("EAS_AF","EMPTY");
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
                ArrayList<String> AFlist = (ArrayList<String>) vctx.getAttribute("AF", "EMPTY");
                ArrayList<String> SAS_AFlist = (ArrayList<String>) vctx.getAttribute("SAS_AF", "EMPTY");
                ArrayList<String> EAS_AFlist = (ArrayList<String>) vctx.getAttribute("EAS_AF", "EMPTY");
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
            ArrayList<String> AFlist = (ArrayList<String>)vctx.getAttribute("AF","EMPTY");
            ArrayList<String> SAS_AFlist = (ArrayList<String>)vctx.getAttribute("SAS_AF","EMPTY");
            ArrayList<String> EAS_AFlist = (ArrayList<String>)vctx.getAttribute("EAS_AF","EMPTY");
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
        for (String aTargetRSIDList : TargetRSIDList) {
            if (vctx.getID().equals(aTargetRSIDList)) {
                /* 手算Allele frequency卡關中QQ，計算AF先用.getAttribute */
                double AF = vctx.getAttributeAsDouble("AF", -100.0);
                System.out.println("AF of " + aTargetRSIDList + ": " + AF);
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
        double TotalChr = vctx.getCalledChrCount();   //5008

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
        return altCount/TotalChr;
    }

//    public List<Pair> call(VariantContext vctx) throws Exception {
//
//        List<String> rsid_AFtype = vctx.getID().concat(vctx.getAttributeAsString());
//        Double alleleFreq = vctx.getAttributeAsDouble("AF", -999.0);
//        System.out.printf("*** %s has AF of %\nf",rsid,alleleFreq);
//
//        return new Pair(rsid,alleleFreq);
//    }

    public static Pair<String,Double> rsID_AF(VariantContext vctx, boolean doPrint){


        String rsid = vctx.getID();
        Double alleleFreq = vctx.getAttributeAsDouble("AF",-999.999);
        // 手算AF
//        Double alleleFreq = 0.0f;
//        float total = vctx.getCalledChrCount();    //5008
//        float count = 0.0f;
//        Allele alt = vctx.getAlternateAllele(0);    // [G]
//        Set<String> sampleNames = vctx.getSampleNames();
//        for(String name: sampleNames){
//            count += vctx.getGenotype(name).countAllele(alt);
//        }
//        alleleFreq = count/total;
        Pair<String, Double> result = new Pair<String, Double>(rsid,alleleFreq);
        if(doPrint) System.out.printf("%s has AF of %f\n",result.getRSID(),result.getAF());
        return result;

    }
    public static List<Pair<String,Double>> getMultiRSID_AF(VariantContext vctx, boolean doPrint){

        int NAlt = vctx.getAlternateAlleles().size();   // usually 2 or 3
        String rsid = vctx.getID();
        List<Pair<String,Double>> results = new LinkedList<>();

        for(int i=0;i<NAlt;i++) {
            Allele allele = vctx.getAlternateAllele(i);
            Double alleleFreq = ((Double) ((ArrayList) vctx.getAttribute("AF")).get(i));
            Pair<String, Double> result = new Pair<String, Double>(rsid, alleleFreq);
            if (doPrint) System.out.printf("%s (%s) has AF of %f\n", result.getRSID(), allele.toString(), result.getAF());
            results.add(i,result);
        }
        return results;

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
        if (!(Objects.equals(vctx.getID(), "."))) {
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

    public static class Pair<String,Double> {

        private final String rsid;
        private final Double af;

        public Pair(String rsid, Double af) {
            this.rsid = rsid;
            this.af = af;
        }

        public String getRSID(){return this.rsid;}
        public Double getAF(){return this.af;}

    }

}


