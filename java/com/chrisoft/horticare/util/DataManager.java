package com.chrisoft.horticare.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.Toast;

import com.chrisoft.horticare.model.Inquiry;
import com.chrisoft.horticare.model.Recipe;
import com.chrisoft.horticare.model.User;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 14/08/2015.
 */
public class DataManager {

    /**
     * Function used to save string to cache
     * @param context
     * @param TEMP_FILE_NAME
     * @param data
     * @return true/false
     */
    public static boolean saveToCache(Context context, String TEMP_FILE_NAME, String data) {
        FileWriter writer=null;
        try {
            /** Getting Cache Directory */
            File cDir = context.getCacheDir();

            /** Getting a reference to temporary file, if created earlier */
            File tempFile = new File(cDir.getPath() + "/" + TEMP_FILE_NAME) ;

            writer = new FileWriter(tempFile);

            /** Saving the contents to the file*/
            writer.write(data);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            /** Closing the writer object */
            try { if (writer!=null) writer.close(); } catch(Exception e) {}
        }
    }

    /**
     * Function used to read file from cache
     * @param context
     * @param TEMP_FILE_NAME
     * @return String
     */
    public static String readFromCache(Context context, String TEMP_FILE_NAME) {
        FileReader fReader=null;
        StringBuilder data = new StringBuilder();
        try {
            /** Getting Cache Directory */
            File cDir = context.getCacheDir();

            /** Getting a reference to temporary file, if created earlier */
            File tempFile = new File(cDir.getPath() + File.separator + TEMP_FILE_NAME) ;

            fReader = new FileReader(tempFile);
            BufferedReader bReader = new BufferedReader(fReader);

            /** Reading the contents of the file , line by line */
            String strLine="";
            while( (strLine=bReader.readLine()) != null) data.append(strLine);

            return data.toString();
        } catch (Exception e) {
            //e.printStackTrace();
            //System.out.println("Error:" + e);
            return null;
        } finally {
            /** Closing the writer object */
            try { if (fReader!=null) fReader.close(); } catch(Exception e) {}
        }
    }

    /**
     * Function used to delete data in cache
     * @param context
     * @param TEMP_FILE_NAME
     * @return true or false
     */
    public static boolean deleteFromCache(Context context, String TEMP_FILE_NAME) {
        try {
            /** Getting Cache Directory */
            File cDir = context.getCacheDir();

            /** Getting a reference to temporary file, if created earlier */
            File tempFile = new File(cDir.getPath() + File.separator + TEMP_FILE_NAME) ;

            if (tempFile.exists()) {
                tempFile.delete();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Function used to save image to cache
     * @param context
     * @param TEMP_FILE_NAME
     * @param image
     * @return true/false
     */
    public static boolean saveImageCache(Context context, String TEMP_FILE_NAME, Bitmap image) {
        return saveToCache(context,TEMP_FILE_NAME, AppUtil.encodeTobase64(image));
    }

    /**
     * Function used to read bitmap from cache
     * @param context
     * @param TEMP_FILE_NAME
     * @return Bitmap
     */
    public static Bitmap getCachedImage(Context context, String TEMP_FILE_NAME) {
        String data = readFromCache(context,TEMP_FILE_NAME);
        if (data!=null && data.length()>0) {
            return AppUtil.decodeBase64(data);
        } else {
            return null;
        }
    }

    /**
     * Function used to parse recipe information from JSON format
     * @param result
     * @param filter
     * @return List
     */
    public static List parseRecipes(Object result, String filter) {
        List recipes = new ArrayList();
        if(result != null && result instanceof JSONArray)
        {
            try
            {
                JSONArray array = (JSONArray)result;

                boolean filterCat=false;
                boolean filterCui=false;
                boolean filterUser=false;
                boolean filterSearch=false;
                for(int x = 0; x < array.length(); x++)
                {
                    if (filter!=null) {
//                        System.out.println("FILTER :: [" + filter + "] :::: [" + array.getJSONObject(x).getString("catId")
//                                + "] :::: [" + array.getJSONObject(x).getString("cuisineId") + "]");
                        if (filter.startsWith("CAT")) {
                            filter = filter.replace("CAT_","");
                            filterCat=true;
                        } else if (filter.startsWith("CUI")) {
                            filter = filter.replace("CUI_","");
                            filterCui=true;
                        } else if (filter.startsWith("USER")) {
                            filter = filter.replace("USER_","");
                            filterUser=true;
                        } else if (filter.startsWith("SEARCH#")) {
                            filter = filter.replace("SEARCH#","");
                            filter = filter.toLowerCase();
                            filterSearch=true;
                        }
                        if (filterCat) {
                            String category = array.getJSONObject(x).getString("catId");
                            if (!filter.equalsIgnoreCase(category)) continue;
                        } else if (filterCui) {
                            String cuisine = array.getJSONObject(x).getString("cuisineId");
                            if (!filter.equalsIgnoreCase(cuisine)) continue;
                        } else if (filterUser) {
                            String id = array.getJSONObject(x).getString("authorId");
                            if (!filter.equalsIgnoreCase(id)) continue;
                        } else if (filterSearch) {
                            String name = array.getJSONObject(x).getString("recipeName");
                            if (name.toLowerCase().indexOf(filter)<0) continue;
                        }
                    }

                    Recipe data = new Recipe(array.getJSONObject(x).getString("recipeId"),
                            array.getJSONObject(x).getString("recipeName"),
                            array.getJSONObject(x).getString("description"),
                            "by " + array.getJSONObject(x).getString("authorName"));
                    data.setIngredients(array.getJSONObject(x).getString("ingredients"));
                    data.setProcedure(array.getJSONObject(x).getString("steps"));
                    data.setRating(array.getJSONObject(x).getString("rating"));
                    data.setCategory(array.getJSONObject(x).getString("catId"));
                    data.setCuisine(array.getJSONObject(x).getString("cuisineId"));
                    data.setAuthorId(array.getJSONObject(x).getString("authorId"));
                    recipes.add(data);
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return recipes;
    }

    /**
     * Function used to parse recipe information from JSON format
     * @param result
     * @param filter
     * @return List
     */
    public static List parseLibrary(Object result, String filter) {
        List library = new ArrayList();
        if(result != null && result instanceof JSONArray)
        {
            try
            {
                JSONArray array = (JSONArray)result;
/*
                boolean filterCat=false;
                boolean filterCui=false;
                boolean filterUser=false;
                boolean filterSearch=false;*/
                for(int x = 0; x < array.length(); x++)
                {
                    /*if (filter!=null) {
//                        System.out.println("FILTER :: [" + filter + "] :::: [" + array.getJSONObject(x).getString("catId")
//                                + "] :::: [" + array.getJSONObject(x).getString("cuisineId") + "]");
                        if (filter.startsWith("CAT")) {
                            filter = filter.replace("CAT_","");
                            filterCat=true;
                        } else if (filter.startsWith("CUI")) {
                            filter = filter.replace("CUI_","");
                            filterCui=true;
                        } else if (filter.startsWith("USER")) {
                            filter = filter.replace("USER_","");
                            filterUser=true;
                        } else if (filter.startsWith("SEARCH#")) {
                            filter = filter.replace("SEARCH#","");
                            filter = filter.toLowerCase();
                            filterSearch=true;
                        }
                        if (filterCat) {
                            String category = array.getJSONObject(x).getString("catId");
                            if (!filter.equalsIgnoreCase(category)) continue;
                        } else if (filterCui) {
                            String cuisine = array.getJSONObject(x).getString("cuisineId");
                            if (!filter.equalsIgnoreCase(cuisine)) continue;
                        } else if (filterUser) {
                            String id = array.getJSONObject(x).getString("authorId");
                            if (!filter.equalsIgnoreCase(id)) continue;
                        } else if (filterSearch) {
                            String name = array.getJSONObject(x).getString("recipeName");
                            if (name.toLowerCase().indexOf(filter)<0) continue;
                        }
                    }

                    Recipe data = new Recipe(array.getJSONObject(x).getString("recipeId"),
                            array.getJSONObject(x).getString("recipeName"),
                            array.getJSONObject(x).getString("description"),
                            "by " + array.getJSONObject(x).getString("authorName"));
                    data.setIngredients(array.getJSONObject(x).getString("ingredients"));
                    data.setProcedure(array.getJSONObject(x).getString("steps"));
                    data.setRating(array.getJSONObject(x).getString("rating"));
                    data.setCategory(array.getJSONObject(x).getString("catId"));
                    data.setCuisine(array.getJSONObject(x).getString("cuisineId"));
                    data.setAuthorId(array.getJSONObject(x).getString("authorId"));
                    library.add(data);*/
                    library.add(array.get(x));
                }
                Constants.COUNT_OF_LIB = library.size();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return library;
    }



    /**
     * Function used to parse user information from JSON format
     * @param json
     * @return User
     * @throws JSONException
     */
    public static User parseUser(String json) throws JSONException {
        JSONArray array = new JSONArray(json);
        //authorId,authorName,password,email
        User user = new User();
        user.setId(array.getJSONObject(0).getString("authorId"));
        user.setName(array.getJSONObject(0).getString("authorName"));
        //user.setAddress("Lipa City, Batangas");
        //user.setCountry("NZL");
        user.setEmail(array.getJSONObject(0).getString("email"));
        user.setPassword(array.getJSONObject(0).getString("password"));
        return user;
    }

    /**
     * Function used to parse cuisines from JSON format
     * @param result
     * @return List
     */
    public static List parseCuisines(Object result) {
        List cuisines = new ArrayList();
        if(result != null && result instanceof JSONArray)
        {
            try
            {
                JSONArray array = (JSONArray)result;

                for(int x = 0; x < array.length(); x++)
                {
                    String[] data = new String[]{ array.getJSONObject(x).getString("cuisineId"),
                            array.getJSONObject(x).getString("cuisineName"), array.getJSONObject(x).getString("recipes")}; ;
                    cuisines.add(data);
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return cuisines;
    }

    /**
     * Function used to parse categories from JSON format
     * @param result
     * @return List
     */
    public static List parseCategories(Object result) {
        List cuisines = new ArrayList();
        if(result != null && result instanceof JSONArray)
        {
            try
            {
                JSONArray array = (JSONArray)result;

                for(int x = 0; x < array.length(); x++)
                {
                    String[] data = new String[]{ array.getJSONObject(x).getString("catId"),
                            array.getJSONObject(x).getString("categoryName"), array.getJSONObject(x).getString("recipes")}; ;
                    cuisines.add(data);
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return cuisines;
    }
    public static List parseFaqs(Object result) {
        List cuisines = new ArrayList();
        if(result != null && result instanceof JSONArray)
        {
            try
            {
                JSONArray array = (JSONArray)result;

                for(int x = 0; x < array.length(); x++)
                {
                    String[] data = new String[]{ array.getJSONObject(x).getString("fid"),
                            array.getJSONObject(x).getString("question"), array.getJSONObject(x).getString("answer")}; ;
                    cuisines.add(data);
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return cuisines;
    }
    public static Inquiry parseInquiry(String json) throws JSONException {
        JSONArray array = new JSONArray(json);
        //authorId,authorName,password,email
        Inquiry inq = new Inquiry();
        inq.setId(array.getJSONObject(0).getString("iid"));
        inq.setName(array.getJSONObject(0).getString("name"));
        //user.setAddress("Lipa City, Batangas");
        //user.setCountry("NZL");
        inq.setEmail(array.getJSONObject(0).getString("email"));
        inq.setPhone(array.getJSONObject(0).getString("phone"));
        inq.setMessage(array.getJSONObject(0).getString("message"));
        return inq;
    }
}


