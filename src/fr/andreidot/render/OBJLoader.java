package fr.andreidot.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class OBJLoader {

    public static RawModel loadObjModel(String fileName, Loader loader) {
        FileReader fr = null;
        try {
            fr = new FileReader(new File("res/obj/" + fileName + ".obj"));
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }

        BufferedReader buff     = new BufferedReader(fr);
        String line;
        List<Vector3f> vertices = new ArrayList<Vector3f>();
        List<Vector3f> normals  = new ArrayList<Vector3f>();
        List<Integer> indices   = new ArrayList<Integer>();
        float[] verticesArray   = null;
        float[] normalsArray    = null;
        int[] indicesArray      = null;

        try {
            while(true) {
                line                    = buff.readLine();
                String[] currentLine    = line.split(" ");

                if(line.startsWith("v ")) {
                    Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]),
                            Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
                    vertices.add(vertex);
                }

                if(line.startsWith("vn ")) {
                    Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]),
                            Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
                    normals.add(normal);
                }

                if(line.startsWith("f ")) {
                    normalsArray = new float[vertices.size() * 2];
                    break;
                }
            }


                while(line != null ) {
                    if(!line.startsWith("f ")) {
                        line = buff.readLine();
                        continue;
                }

                String[] currentLine        = line.split(" ");
                String[] vertex1            = currentLine[1].split("/");
                String[] vertex2            = currentLine[2].split("/");
                String[] vertex3            = currentLine[3].split("/");

                processVertex(vertex1, indices, normals, normalsArray);
                processVertex(vertex2, indices, normals, normalsArray);
                processVertex(vertex3, indices, normals, normalsArray);
                line = buff.readLine();
            }
            buff.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        verticesArray = new float[vertices.size() * 3];
        indicesArray = new int[indices.size()];

        int vertexPointer = 0;
        for(Vector3f vertex:vertices) {
            verticesArray[vertexPointer++] = vertex.x;
            verticesArray[vertexPointer++] = vertex.y;
            verticesArray[vertexPointer++] = vertex.z;
        }

        for(int i = 0; i < indices.size(); i++) {
            indicesArray[i] = indices.get(i);
        }
        return loader.loadToVAO(verticesArray, indicesArray);
    }

    private static void processVertex(String[] vertexData, List<Integer> indices, List<Vector3f> normals,
                                      float[] normalsArray) {

        int currentVertexPointer = Integer.parseInt(vertexData[0]) - 1;
        indices.add(currentVertexPointer);

        Vector3f currentNorm = normals.get(Integer.parseInt(vertexData[2])-1);
        normalsArray[currentVertexPointer * 3]       = currentNorm.x;
        normalsArray[currentVertexPointer * 3 + 1]   = currentNorm.y;
        normalsArray[currentVertexPointer * 3 + 2]   = currentNorm.z;
    }
}
