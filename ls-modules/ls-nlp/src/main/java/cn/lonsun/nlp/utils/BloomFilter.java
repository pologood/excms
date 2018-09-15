package cn.lonsun.nlp.utils;

import java.io.*;
import java.util.BitSet;

public class BloomFilter implements Serializable{
    private static final long serialVersionUID = -5221305273707291280L;
    private static final int BIT_SIZE = 2 << 28 ;//二进制向量的位数，相当于能存储1000万条url左右
    private static final int[] seeds = MisjudgmentRate.MIDDLE.getSeeds();//误报率为万分之一

    private static BloomFilter bloomFilter = null;

    private BitSet bits = new BitSet(BIT_SIZE);
    private Hash[] func = new Hash[seeds.length];//用于存储8个随机哈希值对象


    public static BloomFilter getInstance(){
        if(bloomFilter==null){
            try {
                BloomFilter bloomFilter1 = null;//readFilterFromFile("./bloomFilter.obj");
                if(bloomFilter1==null){
                    bloomFilter1 = new BloomFilter();
                }
                bloomFilter = bloomFilter1;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return bloomFilter;
    }


    private BloomFilter(){
        for(int i = 0; i < seeds.length; i++){
            func[i] = new Hash(BIT_SIZE, seeds[i]);
        }
    }

    public static void main(String[] args) {

//        System.out.println(fileter.addIfNotExist("1111111111111"));
//        System.out.println(fileter.addIfNotExist("2222222222222222"));
//        System.out.println(fileter.addIfNotExist("3333333333333333"));
//        System.out.println(fileter.addIfNotExist("444444444444444"));
//        System.out.println(fileter.addIfNotExist("5555555555555"));
//        System.out.println(fileter.addIfNotExist("6666666666666"));
//        System.out.println(fileter.addIfNotExist("7777777777777"));
//        System.out.println(fileter.addIfNotExist("8888888888888"));
        System.out.println(BloomFilter.getInstance().contains("1111111111111"));
    }

    /**
     * 像过滤器中添加字符串
     */
    public void addValue(String value)
    {
        //将字符串value哈希为8个或多个整数，然后在这些整数的bit上变为1
        if(value != null){
            for(Hash f : func)
                bits.set(f.hash(value), true);
        }

    }

    /**
     * 判断字符串是否包含在布隆过滤器中,不存在则添加进去
     */
    public boolean contains(String value)
    {
        if(value == null)
            return false;

        boolean ret = true;

        //将要比较的字符串重新以上述方法计算hash值，再与布隆过滤器比对
        for(Hash f : func)
            ret = ret && bits.get(f.hash(value));

        if(!ret){
            addValue(value);//将字符串添加进去
//            saveFilterToFile("./bloomFilter.obj");//写入文件
        }

        return ret;
    }

    /**
     * 随机哈希值对象
     */

    public static class Hash{
        private int size;//二进制向量数组大小
        private int seed;//随机数种子

        public Hash(int cap, int seed){
            this.size = cap;
            this.seed = seed;
        }

        /**
         * 计算哈希值(也可以选用别的恰当的哈希函数)
         */
        public int hash(String value){
            int result = 0;
            int len = value.length();
            for(int i = 0; i < len; i++){
                result = seed * result + value.charAt(i);
            }

            return (size - 1) & result;
        }
    }

    /**
     * 写入文件
     * @param path
     */
    public void saveFilterToFile(String path) {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(path));
            oos.writeObject(getInstance());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            if(oos!=null){
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 从文件中读取
     * @param path
     * @return
     */
    public static BloomFilter readFilterFromFile(String path) {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(path));
            return (BloomFilter) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            try {
                if(ois!=null){
                    ois.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 分配的位数越多，误判率越低但是越占内存
     *
     * 4个位误判率大概是0.14689159766308
     *
     * 8个位误判率大概是0.02157714146322
     *
     * 16个位误判率大概是0.00046557303372
     *
     * 32个位误判率大概是0.00000021167340
     *
     * @author lianghaohui
     *
     */
    public enum MisjudgmentRate {
        // 这里要选取质数，能很好的降低错误率
        /**
         * 每个字符串分配4个位
         */
        VERY_SMALL(new int[] { 2, 3, 5, 7 }),
        /**
         * 每个字符串分配8个位
         */
        SMALL(new int[] { 2, 3, 5, 7, 11, 13, 17, 19 }), //
        /**
         * 每个字符串分配16个位
         */
        MIDDLE(new int[] { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53 }), //
        /**
         * 每个字符串分配32个位
         */
        HIGH(new int[] { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97,
                101, 103, 107, 109, 113, 127, 131 });

        private int[] seeds;

        private MisjudgmentRate(int[] seeds) {
            this.seeds = seeds;
        }

        public int[] getSeeds() {
            return seeds;
        }

        public void setSeeds(int[] seeds) {
            this.seeds = seeds;
        }

    }

}