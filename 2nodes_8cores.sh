#!/bin/bash
#SBATCH --time=01:00:00
#SBATCH --nodes=2
#SBATCH --ntasks=8
#SBATCH --tasks-per-node=4
#SBATCH --cpus-per-task=1
module load Java/1.8.0_71
module load mpj/0.44
javac -cp .:$MPJ_HOME/lib/mpj.jar MPIRun.java
mpjrun.sh -np 8 MPIRun