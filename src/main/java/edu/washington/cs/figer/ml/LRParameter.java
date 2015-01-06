package edu.washington.cs.figer.ml;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

import edu.washington.cs.figer.util.Serializer;
import edu.washington.cs.figer.util.Timer;


public class LRParameter extends Parameter {
  /**
	 * 
	 */
  private static final long serialVersionUID = 5403514844833400672L;
  public double[] lambda = null;
  public transient double[] backupLambda = null;

  public static void main(String[] args) {
    LRParameter para = new LRParameter(5142366);
    Random rand = new Random(12);
    for (int i = 0; i < para.lambda.length; i++) {
      if (rand.nextDouble() < 0.1) {
        para.lambda[i] = rand.nextDouble();
      }
    }
    // set backupLambda for serialization in a sparse representation
    para.backupLambda = para.lambda;
    para.lambda = null;
    Serializer.serialize(para, "test2.ser");
    System.out.println(para.lambda.length);
  }

  public LRParameter(int num) {
    lambda = new double[num];
  }

  public void init() {
    if (lambda == null)
      return;
    Random r = new Random((int) (System.currentTimeMillis() % Integer.MAX_VALUE));
    for (int i = 0; i < lambda.length; i++) {
      lambda[i] = r.nextDouble() * var;
    }
  }

  public static double var = 0.2;

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder();
    for (int i = 0; i < lambda.length; i++)
      b.append(lambda[i] + "\n");

    return b.toString();
  }

  @Override
  public void clear() {
    if (lambda != null)
      lambda = new double[lambda.length];
  }


  private void writeObject(ObjectOutputStream out) throws IOException {
    out.defaultWriteObject();
    if (backupLambda != null) {
      System.out.println("not interval ");
      int len = backupLambda.length;
      int nnz = 0;
      for (int i = 0; i < len; i++) {
        if (backupLambda[i] != 0) {
          nnz++;
        }
      }
      out.writeInt(len);
      out.writeInt(nnz);
      for (int i = 0; i < len; i++) {
        if (backupLambda[i] != 0) {
          out.writeInt(i);
          out.writeDouble(backupLambda[i]);
        }
      }
    }
  }

  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    if (lambda == null) {
      int len = in.readInt();
      int nnz = in.readInt();
      lambda = new double[len];
      for (int i = 0; i < nnz; i++) {
        lambda[in.readInt()] = in.readDouble();
      }
    }
  }
}
