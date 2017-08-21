# pharmacogenomics

1. pull 新的 code之後除了看到這個readme file之外, pom.xml同時也會被更新

2. 把filePath 的路徑改掉，不要hard code, 改成讀args[0]
ex:
final String vcfPath = "/Users/yat/vcf/deafness/A0087Y_09182015_bwamem.filtered.haplotype.SnpIndel.vcf";
改成
final String vcfPath = args[0];

3. 在intelliJ的最下面有一個小小的terminal, 點下去之後那個畫面就跟我們一般在操作terminal一樣
4. 在terminal 裡面打 mvn clean package 就會開始import library然後打包jar檔
5. 打包完之後應該會在畫面看到 [INFO] BUILD SUCCESS 之類的訊息
6. 打包好的jar檔案會放在跟code同樣的一個資料夾下面的target資料夾
( 例如: ......./IdeaProjects/C4lab/pharmacogenomics/target/pharmacogenomics-1.0.0.jar )

7. 把jar檔案上傳到207 server (可以用filezilla或是直接用command line scp上去)
( 例如: scp target/pharmacogenomics-1.0.0.jar 你的207帳號@140.112.183.207:/home/你的207帳號/pharmacogenomics-1.0.0.jar)

8. 連線進去server 然後直接用jar -cp指定你要執行的class name, 然後後面接著參數args[0],也就是我們的input資料
(例如: java -cp pharmacogenomics-1.0.0.jar C4lab.VcfReaderYa /home/shared_data/the1000genomes/ALL.chr22.phase3_shapeit2_mvncall_integrated_v5a.20130502.genotypes.vcf)
h
9. 請大家在chr22裡面計算以下8個rsID對應到的allele freq是多少

- rs587638290
- rs587736341
- rs534965407
- rs9609649
- rs5845047
- rs80690
- rs137506
- rs138355780
