package neural_bp_ocr;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

// Original verison
public class Main {

	public static int[][] data;
	// Number of nodes in input layer + bias
	private static final int INPUT_NODE_NUM = 784;
	// Number of nodes in hidden 1
	private static final int H1_NODE_NUM = 200;
	private static double LEARNING_RATE = 0.2;
	private static int epoch = 7;
	private static final int seed = 10;
	// Number of nodes in output layer
	private static final int OUTPUT_NODE_NUM = 10;
	// Layers' sums and outputs
	private static double INPUT[] = new double[INPUT_NODE_NUM];
	private static double H1_NODE_OUTPUTS[] = new double[H1_NODE_NUM];
	private static double H1_NODE_SUM[] = new double[H1_NODE_NUM];
	private static double H1_DELTA[] = new double[H1_NODE_NUM];
	private static double OUTPUT_NODE_OUTPUTS[] = new double[OUTPUT_NODE_NUM];
	private static double OUTPUT_NODE_SUM[] = new double[OUTPUT_NODE_NUM];
	private static double OUTPUT_DELTA[] = new double[OUTPUT_NODE_NUM];
	// Input to hidden 1 weights
	private static double I_TO_H1_WEIGHTS[][] = new double[INPUT_NODE_NUM + 1][H1_NODE_NUM];
	// Hidden 1 to output weights
	private static double H1_TO_O_WEIGHTS[][] = new double[H1_NODE_NUM + 1][OUTPUT_NODE_NUM];
	private static ArrayList<ImageData> imagesTrain = new ArrayList<ImageData>();
	private static ArrayList<ImageData> imagesTest = new ArrayList<ImageData>();
	private static Random r = new Random();
	private static long startTime;
	private static long endTime;

	public static void main(String[] args) throws IOException {

		String outputAddress = "output.txt";

		r.setSeed(seed);
		getInputTraningAndTestSet(args[0], args[1], args[2], args[3]);
		initNetWeights();

		startTime = System.currentTimeMillis();
		bpTraning();
		testNetwork(outputAddress);
		endTime = System.currentTimeMillis();

		long totalTime = endTime - startTime;
		System.out.println("Traning Time: " + totalTime / 60000 + " minutes!");
	}

	private static void getInputTraningAndTestSet(String trainImages,
			String trainLabels, String testImages, String testLabels) {
		imagesTrain = ImageData.readData(trainImages, trainLabels);
		imagesTest = ImageData.readData(testImages, testLabels);
		// imagesTest = ImageData.readData2(testImages);
	}

	private static void initNetWeights() {
		initWeightMartix(I_TO_H1_WEIGHTS);
		initWeightMartix(H1_TO_O_WEIGHTS);
	}

	private static void bpTraning() {
		for (int i = 0; i < epoch; i++) {
			System.out.println("Learning: Epoch#" + (i + 1) + "/" + epoch
					+ " is in progress...");
			for (ImageData image : imagesTrain) {
				resetNetwork();
				INPUT = matrixToArray(image.data);
				normalize(INPUT);
				calculateNetOutput(INPUT);
				backProbError(image.label);
			}
			// LEARNING_RATE = LEARNING_RATE / (1.1);
			System.out.println("Epoch#" + (i + 1) + " was done!");
			System.out.println("------------------------------------");
		}
	}

	private static void normalize(double[] input) {
		for (int i = 0; i < input.length; i++) {
			input[i] = ((input[i] - 0) / (255 - 0)) * (1 - 0) + 0;
		}
	}

	private static void resetNetwork() {
		reset(INPUT);
		reset(H1_NODE_OUTPUTS);
		reset(H1_NODE_SUM);
		reset(H1_DELTA);
		reset(OUTPUT_NODE_OUTPUTS);
		reset(OUTPUT_NODE_SUM);
		reset(OUTPUT_DELTA);
	}

	private static void reset(double[] input) {
		for (int i = 0; i < input.length; i++) {
			input[i] = 0;
		}
	}

	private static void calculateNetOutput(double[] inputImageVector) {
		calculateLayerOutput(addBiasToLayer(inputImageVector), I_TO_H1_WEIGHTS,
				H1_NODE_OUTPUTS, H1_NODE_SUM);
		calculateLayerOutput(addBiasToLayer(H1_NODE_OUTPUTS), H1_TO_O_WEIGHTS,
				OUTPUT_NODE_OUTPUTS, OUTPUT_NODE_SUM);
	}

	private static void calculateLayerOutput(double[] input,
			double[][] weights, double[] output, double[] sum) {
		for (int i = 0; i < output.length; i++) {
			output[i] = 0.0;
			for (int j = 0; j < input.length; j++) {
				output[i] += (input[j] * weights[j][i]);
			}
			sum[i] = output[i];
			output[i] = sigmoid(output[i]);
		}
	}

	private static void backProbError(int label) {
		// Compute delta for output layer
		double ideal[] = new double[10];
		ideal[label] = 1.0;
		for (int i = 0; i < OUTPUT_DELTA.length; i++) {
			OUTPUT_DELTA[i] = sigmoidDer(OUTPUT_NODE_SUM[i])
					* (ideal[i] - OUTPUT_NODE_OUTPUTS[i]);
		}
		// Compute the deltas for preceding layers
		for (int i = 0; i < H1_DELTA.length; i++) {
			double sum = 0;
			for (int j = 0; j < OUTPUT_DELTA.length; j++) {
				sum = H1_TO_O_WEIGHTS[i][j] * OUTPUT_DELTA[j];
			}
			H1_DELTA[i] = sigmoidDer(H1_NODE_SUM[i]) * sum;
		}
		updateWeights(H1_TO_O_WEIGHTS, OUTPUT_DELTA,
				addBiasToLayer(H1_NODE_OUTPUTS));
		updateWeights(I_TO_H1_WEIGHTS, H1_DELTA, addBiasToLayer(INPUT));
	}

	private static void updateWeights(double[][] weight, double[] detla,
			double[] input) {
		double[][] deltaW = new double[weight.length][detla.length];
		for (int i = 0; i < deltaW.length; i++) {
			for (int j = 0; j < deltaW[i].length; j++) {
				deltaW[i][j] = LEARNING_RATE * detla[j] * input[i];
			}
		}
		add(weight, deltaW);
	}

	private static void add(double[][] weightMatrix, double[][] deltaW) {
		for (int i = 0; i < weightMatrix.length; i++) {
			for (int j = 0; j < weightMatrix[i].length; j++) {
				weightMatrix[i][j] += deltaW[i][j];
			}
		}
	}

	private static double sigmoid(double x) {
		return 1 / (1 + Math.exp(-x));
	}

	private static double sigmoidDer(double input) {
		double temp = sigmoid(input);
		return (temp * (1 - temp));
	}

	private static double[] addBiasToLayer(double[] inputImageVector) {
		double[] result = new double[inputImageVector.length + 1];
		for (int i = 0; i < inputImageVector.length; i++) {
			result[i] = inputImageVector[i];
		}
		int i = result.length - 1;
		result[i] = 1;
		return result;
	}

	private static void testNetwork(String outputAddress) throws IOException {
		int percentage = 0;
		File file = new File(outputAddress);
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fileWritter = new FileWriter(file.getName(), true);
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		System.out.println("Testing Netwrok with Sample Test...");
		int i = 0;
		for (ImageData image : imagesTest) {
			i++;
			resetNetwork();
			calculateNetOutput(matrixToArray(image.data));
			if (image.label == max(OUTPUT_NODE_OUTPUTS)) {
				percentage++;
			}
			bufferWritter.write(max(OUTPUT_NODE_OUTPUTS) + "\n");
		}
		bufferWritter.close();
		System.out.println("Testing was done and results save in output.txt");
		System.out.println("Accuracy = " + (percentage * 100) / i + " %");
	}

	private static int max(double[] input) {
		double tmp = input[0];
		int index = 0;
		for (int i = 1; i < input.length; i++) {
			if (input[i] > tmp) {
				tmp = input[i];
				index = i;
			}
		}
		return index;
	}

	public static double[] matrixToArray(int[][] image) {
		double[] inputImageVector = new double[784];
		int index = 0;
		for (int i = 0; i < 28; i++) {
			for (int j = 0; j < 28; j++) {
				inputImageVector[index++] = image[i][j];
			}
		}
		return inputImageVector;
	}

	private static void initWeightMartix(double[][] weightMatrix) {
		for (int i = 0; i < weightMatrix.length; i++) {
			for (int j = 0; j < weightMatrix[i].length; j++) {
				weightMatrix[i][j] = randomNum();
			}
		}
	}

	private static double randomNum() {
		double min = -1;
		double max = 1;

		double result = min + (max - min) * r.nextDouble();
		return result;
	}

	public static void printMartix(double[][] inputMartix) {
		System.out.println(Arrays.deepToString(inputMartix)
				.replaceAll("],", "]\r\n").replaceAll("]]", "]")
				.replace("[[", " ["));
		System.out.println("\n");
	}
}
