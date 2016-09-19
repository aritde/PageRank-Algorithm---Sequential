import java.util.*;
import java.io.*;
import mpi.*;

public class TestMPI
{
	public static void main(String args[])
	{
		MPI.Init(args);
		int rank = MPI.COMM_WORLD.Rank();
		int size = MPI.COMM_WORLD.Size();
		System.out.println("Testing MPI Usage");
		System.out.println(rank);
		System.out.println(size);
		MPI.Finalize();
		//MPI.COMM_WORLD.Send();
		//MPI.COMM_WORLD.Recv();
		//MPI.Allreduce();
		//MPI.Bcast();
	}
}