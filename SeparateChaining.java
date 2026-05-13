package com.hashvis.model.collision;

import java.util.ArrayList;
import java.util.List;

import com.hashvis.model.hashfunc.*;
import com.hashvis.model.table.*;

public class SeparateChaining implements CollisionResolver {
  HashFunction hashFunc;

  @Override
  public List<HashFunction> getHashFunctionFields(DataType dataType) {
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
    ArrayList<HashFunction> result = new ArrayList<HashFunction>();
    result.add(hashFunc);
    return result;
  }

  // Collision resolution inital data
  HashAction action;
  String key;
  Table table;

  private ArrayList<String> getPseudocode(HashAction action) {
    ArrayList<String> pseudocode = new ArrayList<String>();
    pseudocode.add("TODO: Add pseudocode that reflect actual algorithm well");
    pseudocode.add("The actual language, syntax, ... will be defined later");
    return pseudocode;
  }

  // Algorithm's state machine
  private Integer hashValue = null;
  private Row currentRow = null;
  private Item currentItem = null;

  @Override
  public boolean useSeparateChaining() {
    return true;
  }

  @Override
  public List<String> getAlgorithmAndInitalize(HashAction action, String key, Table table) {
    this.action = action;
    this.key = key;
    this.table = table;
    // reset the state machine
    hashValue = null;
    currentRow = null;
    currentItem = null;
    return getPseudocode(action);
  }

  @Override
  public Result nextStep() {
    if (hashValue == null)
      return handleHashing();
    if (currentRow == null)
      return handleBucketSelection();
    return handleTraversal();
  }

  // Resolution steps
  private Result handleHashing() {
    hashValue = hashFunc.compute(key, table.size());
    return new Result("Hash value: " + hashValue, 0);
  }

  private Result handleBucketSelection() {
    currentRow = table.getRow(hashValue);
    return new Result("Accessing bucket index " + hashValue, 0);
  }

  private Result handleTraversal() {
    // If we don't have an item yet, or we just finished one, get the next
    if (currentItem == null)
      currentItem = currentRow.nextItem();

    if (currentItem == null) {
      return handleFinalization();
    }

    // Check if this is the item we are looking for
    if (currentItem.getName().equals(key))
      return processFoundItem();

    // Otherwise, prepare to move to the next item in the next call
    Item itemToHighlight = currentItem;
    currentItem = null; // Reset so next call to handleTraversal calls nextItem()
    return new Result("Checking item: " + itemToHighlight.getName() + " (No match)", 0);
  }

  private Result processFoundItem() {
    if (action == HashAction.INSERT) {
      return new Result("Error: Duplicate key " + key, -1);
    } else if (action == HashAction.DELETE) {
      currentRow.removeItem(currentItem);
      return new Result("Deleted key " + key, -1);
    } else {
      return new Result("Found key " + key, -1);
    }
  }

  private Result handleFinalization() {
    if (action == HashAction.INSERT) {
      currentRow.addItem(key);
      return new Result("Key not found. Inserted " + key + " into bucket " + hashValue, -1);
    }
    return new Result("Error: Key " + key + " not found in table", -1);
  }
}
