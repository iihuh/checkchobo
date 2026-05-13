package com.hashvis.model.collision;

import java.util.ArrayList;
import java.util.List;

import com.hashvis.model.hashfunc.*;
import com.hashvis.model.table.*;

public abstract class OpenAddressing implements CollisionResolver {
  HashFunction hashFunc;
  protected Integer hashValue = null;
  protected Integer probeCount = 0;
  protected Row currentRow = null;
  protected Integer availableRow = null;
  // Collision resolution inital data
  protected HashAction action;
  protected String key;
  protected Table table;

  public void getHashFunction(DataType dataType) {
    switch (dataType) {
      case INTEGER:
        hashFunc = new HashFunctionNumber();
        break;
      case STRING:
        hashFunc = new HashFunctionString();
        break;
      default:
        break;
    }
  }
  @Override
  public List<HashFunction> getHashFunctionFields(DataType dataType) {
    ArrayList<HashFunction> result = new ArrayList<HashFunction>();
    getHashFunction(dataType);
    result.add(hashFunc);
    return result;
  }
  protected ArrayList<String> getPseudocode(HashAction action) {
    ArrayList<String> pseudocode = new ArrayList<String>();
    pseudocode.add("TODO: Add pseudocode that reflect actual algorithm well");
    pseudocode.add("The actual language, syntax, ... will be defined later");
    return pseudocode;
  }

  @Override
  public boolean useSeparateChaining() {
    return false;
  }

  public List<String> default_getAlgorithmAndInitalize(HashAction action, String key, Table table) {
    this.action = action;
    this.key = key;
    this.table = table;
    // reset the state machine
    hashValue = null;
    probeCount = 0;
    currentRow = null;
    availableRow = null;
    return getPseudocode(action);
  }

  @Override
  public Result nextStep(){
    if (hashValue == null)
      return handleHashing();
    return loop();
  }
  public Result loop() {
    if (currentRow == null)
      return handleBucketSelection();
    Item item = currentRow.nextItem();
    if (item == null)
      return handleFinalization();
    int ind = currentRow.getIndex();
    currentRow = null;
    if (item.isGhosted()) {
      if (availableRow == null) {
        availableRow = ind;
        return new Result("Marking bucket index " + ind + " as available", 0);
      }
    } else if (item.getName().equals(key))
      return processFoundItem(item);
    return new Result("Checking item: " + item.getName() + " (No match)", 0);
  }

  protected Result handleHashing() {
    hashValue = hashFunc.compute(key, table.size());
    return new Result("Hash value: " + hashValue, 0);
  }

  abstract protected Result handleBucketSelection();

  protected Result processFoundItem(Item item) {
    if (action == HashAction.INSERT) {
      return new Result("Error: Duplicate key " + key, -1);
    } else if (action == HashAction.DELETE) {
      item.ghost();
      return new Result("Deleted key " + key, -1);
    } else {
      return new Result("Found key " + key, -1);
    }
  }

  protected Result handleFinalization() {
    if (action == HashAction.INSERT) {
      if (availableRow != null)
        currentRow = table.getRow(availableRow);
      if (currentRow.getItems().size() != 0)
        currentRow.removeItem(currentRow.getItems().get(0));
      currentRow.addItem(key);
      return new Result("Key not found. Inserted " + key + " into bucket " + currentRow.getIndex(), -1);
    }
    return new Result("Error: Key " + key + " not found in table", -1);
  }
}
