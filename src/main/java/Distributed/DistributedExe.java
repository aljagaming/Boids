package Distributed;

import Executive.ExecutionInterface;
import Executive.Variables;
import Gui.Logger;
import mpi.*;

public class DistributedExe implements ExecutionInterface {

    Variables variables;

    public DistributedExe(Variables variables) {
        this.variables = variables;
    }


    @Override
    public void start(String[] args) {


        MPI.Init(args);
        int rank=MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        System.out.println("Hello from process " + rank + " out of " + size);
        Logger.getInstance().log("Hello from process " + rank + " out of " + size);
        MPI.Finalize();


    }

    @Override
    public void stop() {
    }
}
