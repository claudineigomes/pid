package com.example;

import com.example.pseimage.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
@Scope("prototype")
@RequestMapping("/rest")
public class ImagemController {

    @RequestMapping(value = "/imagem", method = RequestMethod.POST)
    public
    @ResponseBody
    ResponseEntity<?> inserirImagem(@RequestParam(name = "arquivo", required = true) MultipartFile file,
                                    @RequestParam(name = "codigo", required = true) String codigo) {

        String root = "/home/claudinei/Documentos/pidImagens/";

        File folderImage = new File(root + codigo);
        if (folderImage.exists()) {
            folderImage.delete();
        }

        folderImage.mkdir();


        System.out.println(codigo);

        if (file.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        File imageFile = new File(folderImage.getPath() + "/" + file.getOriginalFilename());
        Path path = null;
        try {
            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            path = Paths.get(folderImage.getPath() + "/" + file.getOriginalFilename());
            Files.write(path, bytes);

        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject json = new JSONObject();
        try {
            json.put("path", path.toString());
            json.put("nomeImagem", file.getOriginalFilename());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(json.toString());

    }

    @RequestMapping(value = "/funcoes", method = RequestMethod.POST)
    public
    @ResponseBody
    ResponseEntity<?> inserirImagem(@RequestBody Map<String, Object> map) {
        String root = "/home/claudinei/Documentos/pidImagens/";
        String codeImagemOriginal = (String) map.get("codeImagemOriginal");
        String nomeImagem = (String) map.get("nomeImagem");
        String imageFile = root + codeImagemOriginal + "/" + nomeImagem;
        System.out.println(map.toString());


        JSONArray jsonArray3 = new JSONArray((ArrayList)map.get("funcoes"));
        System.out.println(jsonArray3.length());
        File folderImage = new File(root + codeImagemOriginal);

        String lastUsed = "";
        System.out.println("Array " + jsonArray3);

        List<String> params = null;

        JSONArray jsonArray = new JSONArray();

        for(int i = 0; i < jsonArray3.length(); i++){
            System.out.println("ENTROU 1 " + jsonArray3.length());
            try {
                Object o = jsonArray3.get(i);
                if(!o.equals("")){
                    JSONObject object = (JSONObject) o;
                    System.out.println("Object " + object);
                    String nome = object.getString("nome");
                    String[] args2 = {imageFile+lastUsed, "3", "3", "box", "1", "1", "1", "1", "1", "1", "1", "1", "1"};
                    params = new ArrayList<>();
                    for(int j=0; j<args2.length; j++){
                        params.add(args2[j]);
                    }
                    System.out.println(imageFile+lastUsed);
                    if(nome.equals("Negativo")){
                        System.out.println("Negativo");
                        new DigitalNegative().filter(params);
                        lastUsed += "_negative";

                        File file = new File(imageFile+lastUsed);

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id" , i);
                        jsonObject.put("fileName", file.getName());
                        jsonObject.put("url" , "http://localhost:5000/imagens/" + codeImagemOriginal + "/" + file.getName());
                        jsonObject.put("nomeTransformacao", "Negativo Digital");
                        jsonObject.put("descricaoTransformacao", "O negativo digital ...");

                        jsonArray.put(jsonObject);
                    }
                    else if(nome.equals("Equalização de Histograma")){
                        System.out.println("Equalização");
                        new HistogramEqualization().filter(params);
                        lastUsed += "_histogram";

                        File file = new File(imageFile+lastUsed);

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id" , i);
                        jsonObject.put("fileName", file.getName());
                        jsonObject.put("url" , "http://localhost:5000/imagens/" + codeImagemOriginal + "/" + file.getName());
                        jsonObject.put("nomeTransformacao", "Equalização de Histrograma");
                        jsonObject.put("descricaoTransformacao", "O histograma equalizado ...");

                        int[] h = Filter.getHistogram(imageFile + lastUsed);
                        try {
                            jsonObject.put("histogramaEqualizado", Arrays.toString(h));
                            jsonArray.put(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        jsonArray.put(jsonObject);
                    }
                    else if(nome.equals("Laplace")){
                        System.out.println("Laplace");
                        new Laplace().filter(params);
                        lastUsed += "_laplace_mask " + params.get(1)+"x"+params.get(2);

                        File file = new File(imageFile+lastUsed);

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id" , i);
                        jsonObject.put("fileName", file.getName());
                        jsonObject.put("url" , "http://localhost:5000/imagens/" + codeImagemOriginal + "/" + file.getName());
                        jsonObject.put("nomeTransformacao", "Laplace");
                        jsonObject.put("descricaoTransformacao", "A transformação de Laplace ...");
                        jsonArray.put(jsonObject);
                    }
                    else if(nome.equals("Logarítmica")){
                        System.out.println("Logarítmica");
                        params.set(1, object.getString("c"));
                        new Logarithmic().filter(params);
                        lastUsed += "_logarithmic";

                        File file = new File(imageFile+lastUsed);

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id" , i);
                        jsonObject.put("fileName", file.getName());
                        jsonObject.put("url" , "http://localhost:5000/imagens/" + codeImagemOriginal + "/" + file.getName());
                        jsonObject.put("nomeTransformacao", "Logarítmica");
                        jsonObject.put("descricaoTransformacao", "A transformação Logarítmica ...");
                        jsonArray.put(jsonObject);
                    }
                    else if(nome.equals("Potência")){
                        System.out.println("Potencia");
                        params.set(1, object.getString("c"));
                        params.set(2, object.getString("gama"));
                        new Potency().filter(params);
                        lastUsed += "_potency";

                        File file = new File(imageFile+lastUsed);

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id" , i);
                        jsonObject.put("fileName", file.getName());
                        jsonObject.put("url" , "http://localhost:5000/imagens/" + codeImagemOriginal + "/" + file.getName());
                        jsonObject.put("nomeTransformacao", "Potência");
                        jsonObject.put("descricaoTransformacao", "A transformação de Potência ...");
                        jsonArray.put(jsonObject);
                    }
                    else if(nome.equals("Mediana")){
                        System.out.println("Mediana");
                        new Median().filter(params);
                        lastUsed += "_median";

                        File file = new File(imageFile+lastUsed);

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id" , i);
                        jsonObject.put("fileName", file.getName());
                        jsonObject.put("url" , "http://localhost:5000/imagens/" + codeImagemOriginal + "/" + file.getName());
                        jsonObject.put("nomeTransformacao", "Mediana");
                        jsonObject.put("descricaoTransformacao", "A transformação de Mediana ...");
                        jsonArray.put(jsonObject);
                    }
                    else if(nome.equals("MIN")){
                        System.out.println("MIN");
                        new Min().filter(params);
                        lastUsed += "_min";

                        File file = new File(imageFile+lastUsed);

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id" , i);
                        jsonObject.put("fileName", file.getName());
                        jsonObject.put("url" , "http://localhost:5000/imagens/" + codeImagemOriginal + "/" + file.getName());
                        jsonObject.put("nomeTransformacao", "MIN");
                        jsonObject.put("descricaoTransformacao", "A transformação de MIN ...");
                        jsonArray.put(jsonObject);
                    }
                    else if(nome.equals("MAX")){
                        System.out.println("MAX");
                        new Max().filter(params);
                        lastUsed += "_max";

                        File file = new File(imageFile+lastUsed);

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id" , i);
                        jsonObject.put("fileName", file.getName());
                        jsonObject.put("url" , "http://localhost:5000/imagens/" + codeImagemOriginal + "/" + file.getName());
                        jsonObject.put("nomeTransformacao", "MAX");
                        jsonObject.put("descricaoTransformacao", "A transformação de MAX ...");
                        jsonArray.put(jsonObject);
                    }else if(nome.equals("Soma")){
                        System.out.println("Soma");
                        params.set(1, object.getString("imagem2"));
                        new SumImage().filter(params);
                        String[] aux = params.get(1).split("/");
                        String nomeAux = aux[aux.length - 1];
                        lastUsed += "_sum" + nomeAux;

                        File file = new File(imageFile+lastUsed);

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id" , i);
                        jsonObject.put("fileName", file.getName());
                        jsonObject.put("url" , "http://localhost:5000/imagens/" + codeImagemOriginal + "/" + file.getName());
                        jsonObject.put("nomeTransformacao", "Soma");
                        jsonObject.put("descricaoTransformacao", "A transformação de Soma ...");
                        jsonArray.put(jsonObject);
                    }else if(nome.equals("Subtração")){
                        System.out.println("Subtração");
                        params.set(1, object.getString("imagem2"));
                        new SubtractImage().filter(params);
                        String[] aux = params.get(1).split("/");
                        String nomeAux = aux[aux.length - 1];
                        lastUsed += "_subtract"+ nomeAux;

                        File file = new File(imageFile+lastUsed);

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id" , i);
                        jsonObject.put("fileName", file.getName());
                        jsonObject.put("url" , "http://localhost:5000/imagens/" + codeImagemOriginal + "/" + file.getName());
                        jsonObject.put("nomeTransformacao", "Subtração");
                        jsonObject.put("descricaoTransformacao", "A transformação de Subtração ...");
                        jsonArray.put(jsonObject);
                    }else if(nome.equals("Tresholding")){
                        System.out.println("Tresholding");
                        params.set(1, object.getString("limiar"));
                        params.set(2, object.getString("cor1"));
                        params.set(3, object.getString("cor2"));
                        new Tresholding().filter(params);
                        lastUsed += "_tresholding";

                        File file = new File(imageFile+lastUsed);

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id" , i);
                        jsonObject.put("fileName", file.getName());
                        jsonObject.put("url" , "http://localhost:5000/imagens/" + codeImagemOriginal + "/" + file.getName());
                        jsonObject.put("nomeTransformacao", "Tresholding");
                        jsonObject.put("descricaoTransformacao", "A transformação de Tresholding ...");
                        jsonArray.put(jsonObject);
                    }


//                    new GenericMask().filter(params);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        File[] listOfFiles = folderImage.listFiles();

        if (folderImage.exists()) {
            try {
                for (int i = 0; i < listOfFiles.length; i++) {
                    if (listOfFiles[i].isFile()) {
                        String fileName = listOfFiles[i].getName();
                        if(fileName.endsWith(nomeImagem + "_grayscale")){
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("id" , i);
                            jsonObject.put("fileName", fileName);
                            jsonObject.put("url" , "http://localhost:5000/imagens/" + codeImagemOriginal + "/" + listOfFiles[i].getName());
                            jsonObject.put("nomeTransformacao", "Tons de Cinza");
                            jsonObject.put("descricaoTransformacao", "A imagem em tons de cinza ...");
                            jsonArray.put(jsonObject);
                        }
                        else if(fileName.equals(nomeImagem)){
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("id" , i);
                            jsonObject.put("fileName", fileName);
                            jsonObject.put("url" , "http://localhost:5000/imagens/" + codeImagemOriginal + "/" + listOfFiles[i].getName());
                            jsonObject.put("nomeTransformacao", "Imagem Original");
                            jsonObject.put("descricaoTransformacao", "A imagem original ...");
                            jsonArray.put(jsonObject);
                        }
                    } else if (listOfFiles[i].isDirectory()) {
                        System.out.println("Directory " + listOfFiles[i].getName());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        int[] h = Filter.getHistogram(imageFile);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("histogramaImagemOriginal", Arrays.toString(h));
            jsonArray.put(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(jsonArray.toString());

    }

}