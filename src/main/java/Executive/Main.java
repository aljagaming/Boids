package Executive;

import mpi.*;

public class Main {
    public static void main(String[] args) {

        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        System.out.println("Hello my rank is "+rank);

        if (rank==0) {
            System.out.println("Hello from MPJ!");
            Variables variables = new Variables(args);
            variables.updateExecutionStyle(ExecutionStyle.SEQUENTIAL);

        }else{
            int msg[]=new int[1];
            MPI.COMM_WORLD.Recv(msg,0,1,MPI.INT,0,0);
            System.out.println("------------------------------");
            System.out.println("Rank " + rank + ": received start signal, doing work now.");
            System.out.println("Distributed"+rank);
        }

        MPI.Finalize();
    }
}
