package Parallel;

import Executive.ExecutionInterface;
import Executive.Variables;

public class ParallelExe implements ExecutionInterface {

    Variables variables;

    public ParallelExe(Variables variables) {
        this.variables = variables;
    }

    @Override
    public void start() {
        //one thread
        //take gui
        //execute gui

    }

    @Override
    public void stop() {

    }
}
