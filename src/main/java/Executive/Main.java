package Executive;

import Distributed.Recievers;
import Distributed.WorkersGrid;
import FPS.Clock;
import Gui.BoidField;
import mpi.*;

import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();

        if (rank == 0) {


            Variables variables = new Variables();
            variables.updateExecutionStyle(ExecutionStyle.SEQUENTIAL);



        } else {
            //RECIEVERS

            WorkersGrid grid=null;
            grid=Recievers.workerCp(null);



            while (true) {
                grid=Recievers.workerCp(grid);
            }


        }

    }
}
