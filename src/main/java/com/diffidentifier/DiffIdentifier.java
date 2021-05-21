package com.diffidentifier;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.json.XML;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.yaml.snakeyaml.Yaml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class DiffIdentifier {

    public static JSONObject getPropertyFileDiff (String fileOne,String fileTwo) throws Exception
    {
        Properties oldProperties = new Properties();
        Properties newProperties = new Properties();
        try(FileInputStream oldFis = new FileInputStream(fileOne);
            FileInputStream newFis = new FileInputStream(fileTwo)){
            oldProperties.load(oldFis);
            newProperties.load(newFis);
            Map<String, String> oldPropMap = new HashMap(oldProperties);
            Map<String, String> newPropMap = new HashMap(newProperties);
            MapDifference diff = Maps.difference(oldPropMap, newPropMap);
            Map left = diff.entriesOnlyOnLeft();
            Map difference =diff.entriesDiffering();
            Map right = diff.entriesOnlyOnRight();
            return DiffUtil.constructDiffObj(left,difference,right);
        }
    }

    public static JSONObject getYMLFileDiff (String fileOne, String fileTwo) throws Exception  {
        try(FileInputStream newFis = new FileInputStream(fileOne);
            FileInputStream oldFis = new FileInputStream(fileTwo)) {
            Map newyamlMap =  DiffUtil.flatten((Map) new Yaml().load(newFis));
            Map oldyamlMap =  DiffUtil.flatten((Map) new Yaml().load(oldFis));
            MapDifference diff = Maps.difference(oldyamlMap, newyamlMap);
            Map left = diff.entriesOnlyOnLeft();
            Map difference = diff.entriesDiffering();
            Map right = diff.entriesOnlyOnRight();
            return DiffUtil.constructDiffObj(left,difference,right);
        }
    }

    public static JSONObject getXMLFileDiff (String fileOne, String fileTwo) throws Exception  {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document newDoc = docBuilder.parse(fileOne);
            Document oldDoc = docBuilder.parse(fileTwo);
            org.json.JSONObject oldJson = XML.toJSONObject(DiffUtil.toXmlString(oldDoc));
            org.json.JSONObject newJson = XML.toJSONObject(DiffUtil.toXmlString(newDoc));
            Gson gson = new Gson();
            Map firstmap = gson.fromJson(oldJson.toString(), LinkedHashMap.class);
            Map secondmap = gson.fromJson(newJson.toString(), LinkedHashMap.class);
            MapDifference diff = Maps.difference(DiffUtil.flatten(firstmap), DiffUtil.flatten(secondmap));
            Map left = diff.entriesOnlyOnLeft();
            Map difference = diff.entriesDiffering();
            Map right = diff.entriesOnlyOnRight();
            return DiffUtil.constructDiffObj(left,difference,right);
    }
}
