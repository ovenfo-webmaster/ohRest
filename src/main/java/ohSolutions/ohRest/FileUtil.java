package ohSolutions.ohRest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import ohSolutions.ohRest.util.bean.FileBean;

public class FileUtil {

	public static boolean uploadFiles(JSONArray data) throws Exception {
		
		for(int i = 0; i < data.length(); i++) {
			try {
				FileUtil.newFile((JSONObject) data.get(i));
			} catch(Exception e){
				e.printStackTrace();
				throw new Exception("Error al subir el archivo en la linea  "+i);
			}
		}
		
		return true;
	}
	
	public static boolean deleteFiles(JSONArray data) throws Exception {
		
		for(int i = 0; i < data.length(); i++) {
			try {
				FileUtil.deleteFile((JSONObject) data.get(i));
			} catch(Exception e){
				e.printStackTrace();
				throw new Exception("Error al eliminar el archivo en la linea  "+i);
			}
		}
		 
		return true;
	}
	
	public static boolean deleteFolderAll(JSONArray data) throws Exception {
		
		for(int i = 0; i < data.length(); i++) {
			try {
				FileUtil.deleteFolderAll((JSONObject) data.get(i));
			} catch(Exception e){
				e.printStackTrace();
				throw new Exception("Error al eliminar el folder de la linea "+i);
			}
		}
		
		return true;
	}
	
	private static boolean newFile(JSONObject file) throws IOException {

		FileBean fileSave = new FileBean();
		
		fileSave.setName(file.getString("name"));
		fileSave.setRewritable(file.getBoolean("isRewritable"));
		fileSave.setSource(file.getString("source"));
		fileSave.setUrl(file.getString("url"));

		return newFile(fileSave);

	}
	
	private static boolean newFile(FileBean fileSave) throws IOException {

		File fileUrl = new File(fileSave.getUrl());
		if(!fileUrl.exists()) {
			fileUrl.mkdirs();
		}

		if(fileSave.getName() != null && fileSave.getName().length() > 0) {
			File file = new File(fileSave.getUrl()+File.separator+fileSave.getName());
			if(file.exists() && fileSave.isRewritable()){
				file.delete();
			}

			if((file.exists() && fileSave.isRewritable()) || !file.exists()){
				BufferedWriter bwProcesoController =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8")); // Remove FileOutputStream(file, true)
				bwProcesoController.write(fileSave.getSource());
				bwProcesoController.close();
			}
		}

		return true;

	}
	
	private static boolean deleteFile(JSONObject file) throws IOException {

		FileBean fileSave = new FileBean();
		
		fileSave.setName(file.getString("name"));
		fileSave.setUrl(file.getString("url"));

		return deleteFile(fileSave);

	}
	
	private static boolean deleteFolderAll(JSONObject file) throws IOException {

		FileBean fileSave = new FileBean();
		
		fileSave.setName(file.getString("name"));
		fileSave.setUrl(file.getString("url"));

		return deleteFolderAll(fileSave);

	}
	
	private static boolean deleteFile(FileBean fileSave) throws IOException {

		File file = new File(fileSave.getUrl()+File.separator+fileSave.getName());
		if(file.exists()){
			file.delete();
		}
		File folder = new File(fileSave.getUrl());
		if(folder.exists() && folder.list().length == 0) {
			folder.delete();
		}
		
		return true;

	}
	
	private static boolean deleteFolderAll(FileBean fileSave) throws IOException {
		File file = new File(fileSave.getUrl()+File.separator+fileSave.getName());
		FileUtils.deleteDirectory(file);
		return true;
	}
	
	public static String getFile(String fileStr) throws IOException {

        StringBuilder contentBuilder = new StringBuilder();
        
        try (Stream<String> stream = Files.lines( Paths.get(fileStr), StandardCharsets.UTF_8))
        {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
 
        return contentBuilder.toString();

	}
	
}