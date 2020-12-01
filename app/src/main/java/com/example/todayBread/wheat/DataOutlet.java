/**
 * Author: wheat (github.com/wheat2018)
 * Date: 2020-11-25
 */

package com.example.todayBread.wheat;

import android.util.Log;


import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Function;

/**
 * 数据批发器。
 * 依赖于一个数据迭代器，可选依赖于一个数据包装器。当指定一个批发数量n时，DataOutlet向数据迭代器
 * 尝试迭代n次，并用数据包装器将每个返回值进行包装，最终返回不大于n的包装数据数组。此过程可以是异步的。
 */
public class DataOutlet{
    private final Iterator<Object> dataIterator;
    private final ArrayBlockingQueue<Object> packedDataBuffer;

    /**
     * 构造函数。
     * @param dataIterator 数据迭代器。
     * @param dataPacker 数据包装器。
     * @param capacity 包装数据缓冲队列最大容量。
     */
    public DataOutlet(@NotNull Iterator<Object> dataIterator, Function<Object, Object> dataPacker, int capacity){
        this.dataIterator = dataIterator;
        this.packedDataBuffer = new ArrayBlockingQueue<>(capacity);
        new Thread(() -> {
            while (dataIterator.hasNext()) {
                try {
                    packedDataBuffer.put(dataPacker == null ?
                            dataIterator.next() : dataPacker.apply(dataIterator.next()));
                } catch (InterruptedException e) {
                    Log.e("DataOutletProductThread", e.toString());
                }
            }
        }).start();
    }

    public DataOutlet(Iterator<Object> dataIterator, Function<Object, Object> dataPacker){
        this(dataIterator, dataPacker, 16);
    }

    /**
     * 判断数据批发器是否还有剩余数据。
     * @return true表示仍有剩余数据，false表示缓冲池为空且数据迭代器不可再迭代。
     */
    public boolean empty(){
        return !dataIterator.hasNext() && packedDataBuffer.isEmpty();
    }

    /**
     * 获取当前数据缓冲队列数据个数。
     * @return 当前数据缓冲队列数据个数。
     */
    public int bufferCount() { return packedDataBuffer.size(); }

    /**
     * 测试批发器是否已准备好指定批量大小的数据，若已准备好，调用getBatch能立即返回。
     * @param batchSize 批量大小。
     * @return 是否已准备好。
     */
    public boolean testBatch(int batchSize){
        return !dataIterator.hasNext() || packedDataBuffer.size() >= batchSize;
    }

    /**
     * 同步获取批量数据。
     * @param batchSize 批量大小。
     * @return 数据批。
     */
    public Object[] getBatch(int batchSize){
        ArrayList<Object> batch = new ArrayList<>(batchSize);
        packedDataBuffer.drainTo(batch, batchSize);
        while (dataIterator.hasNext() && batch.size() < batchSize){
            try {
                batch.add(packedDataBuffer.take());
            } catch (InterruptedException e) {
                Log.e("getBatch", e.toString());
            }
        }
        return batch.toArray();
    }

    /**
     * 异步获取批量数据，完成时调用回调函数。
     * @param batchSize 批量大小。
     * @param callback 完成时回调。要求一个以Object[]为唯一参数的Lambda。
     */
    public void asyncGetBatch(int batchSize, com.example.todayBread.wheat.Utils.Function<Object[]> callback){
        if (testBatch(batchSize)) callback.run(getBatch(batchSize));
        else new Thread(() -> callback.run(getBatch(batchSize))).start();
    }
}

/**
 * 将某些数组转换成可迭代对象。
 */
class IteratorConvert{
    static class JSONArrayIterator implements Iterator<Object>{
        private final JSONArray array;
        private int idx = 0;
        public JSONArrayIterator(JSONArray array){
            this.array = array;
        }

        @Override
        public boolean hasNext() {
            return array.length() > idx;
        }

        @Override
        public Object next() {
            try {
                Object obj = array.get(idx);
                ++idx;
                return obj;
            } catch (JSONException e) {
                throw new NoSuchElementException();
            }
        }
    }
}