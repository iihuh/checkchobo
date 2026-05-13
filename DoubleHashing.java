package com.hashvis.model.collision;

import java.util.ArrayList;
import java.util.List;

import com.hashvis.model.hashfunc.HashFunction;
import com.hashvis.model.table.Table;

public class DoubleHashing extends OpenAddressing {
  protected Integer hashValue1 = null;
  protected Integer hashValue2 = null;
  Result tmp;
  @Override
  public List<HashFunction> getHashFunctionFields(DataType dataType) {
    ArrayList<HashFunction> result = new ArrayList<HashFunction>();
    getHashFunction(dataType);
    result.add(hashFunc);
    result.add(hashFunc);
    return result;
  }
  @Override
  public List<String> getAlgorithmAndInitalize(HashAction action, String key, Table table) {
    hashValue1 = null;
    hashValue2 = null;
    return default_getAlgorithmAndInitalize(action,key,table);
  }
  @Override
  protected Result handleBucketSelection() {
    if (probeCount == table.size())
      return handleFinalization();
    currentRow = table.getRow((hashValue1 + probeCount * hashValue2) % table.size());
    probeCount++;
    return new Result("Accessing bucket index " + currentRow.getIndex(), 0);
  }
  @Override
  public Result nextStep() {
    if (hashValue1 == null){
      tmp = handleHashing();
      hashValue1 = hashValue;
      return tmp;
    }
    if (hashValue2 == null){
      tmp = handleHashing();
      hashValue2 = hashValue;
      if (hashValue2==0){hashValue2=1;}
      return tmp;
    }
    return loop();
  }
}