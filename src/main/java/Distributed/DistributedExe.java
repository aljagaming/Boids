package Distributed;

import Executive.ExecutionInterface;
import Executive.Variables;


public class DistributedExe implements ExecutionInterface {

    Variables variables;

    public DistributedExe(Variables variables) {
        this.variables = variables;
    }


    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }
}
