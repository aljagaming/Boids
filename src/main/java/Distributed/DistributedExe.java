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

        int size=MPI.COMM_WORLD.Size();
        int[] msg = new int[] {1};
        for (int i = 1; i < size; i++) {
            MPI.COMM_WORLD.Send(msg, 0, 1, MPI.INT, i, 0);
        }

    }

    @Override
    public void stop() {
    }
}
