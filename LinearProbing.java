package com.hashvis.model.collision;

import java.util.List;

import com.hashvis.model.table.Table;

public class LinearProbing extends OpenAddressing {
  @Override
  public List<String> getAlgorithmAndInitalize(HashAction action, String key, Table table) {
    return default_getAlgorithmAndInitalize(action,key,table);
  }
  @Override
  protected Result handleBucketSelection() {
    if (probeCount == table.size())
      return handleFinalization();
    currentRow = table.getRow((hashValue + probeCount) % table.size());
    probeCount++;
    return new Result("Accessing bucket index " + currentRow.getIndex(), 0);
  }
}