package cn.lonsun.lottery.internal.utils;

/**
 * Created by lonsun on 2017-1-13.
 */
public class Result {

    private int index;
    private int sumTime;
    private int time;
    private double probability;
    private double realProbability;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getSumTime() {
        return sumTime;
    }

    public void setSumTime(int sumTime) {
        this.sumTime = sumTime;
    }

    public double getProbability() {
        return probability;
    }

    public double getRealProbability() {
        return realProbability;
    }

    public void setRealProbability(double realProbability) {
        this.realProbability = realProbability;
    }

    public Result(){

    }

    public Result(int index, int sumTime, int time, double realProbability) {
        this.setIndex(index);
        this.setTime(time);
        this.setSumTime(sumTime);
        this.setRealProbability(realProbability);

    }

    public String toString(){
        return "索引值：" + index + "，抽奖总数：" + sumTime + "，抽中次数：" + time + "，概率："
                + realProbability + "，实际概率：" + (double)time/sumTime;
    }
}
