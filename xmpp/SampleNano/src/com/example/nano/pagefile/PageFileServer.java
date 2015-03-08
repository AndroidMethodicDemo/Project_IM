package com.example.nano.pagefile;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.Target;
import java.net.URL;
import java.net.URLDecoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.example.nano.NanoHTTPD;


public class PageFileServer extends NanoHTTPD {
//	private Context context;
//    public PageFileServer(int port, Context context) {
//		super(port);
//		this.quiet = false;
//		this.context = context;
//	}
    public PageFileServer(int port) {
		super(port);
		this.quiet = false;
	}

	/**
     * Common mime type for dynamic content: binary
     */
    public static final String MIME_DEFAULT_BINARY = "application/octet-stream";

    /**
     * Hashtable mapping (String)FILENAME_EXTENSION -> (String)MIME_TYPE
     */
    @SuppressWarnings("serial")
	private static final Map<String, String> MIME_TYPES = new HashMap<String, String>() {{
        put("css", "text/css");
        put("htm", "text/html");
        put("html", "text/html");
        put("xml", "text/xml");
        put("java", "text/x-java-source, text/java");
        put("md", "text/plain");
        put("txt", "text/plain");
        put("asc", "text/plain");
        put("gif", "image/gif");
        put("jpg", "image/jpeg");
        put("jpeg", "image/jpeg");
        put("png", "image/png");
        put("mp3", "audio/mpeg");
        put("m3u", "audio/mpeg-url");
        put("mp4", "video/mp4");
        put("ogv", "video/ogg");
        put("flv", "video/x-flv");
        put("mov", "video/quicktime");
        put("swf", "application/x-shockwave-flash");
        put("js", "application/javascript");
        put("pdf", "application/pdf");
        put("doc", "application/msword");
        put("ogg", "application/x-ogg");
        put("zip", "application/octet-stream");
        put("exe", "application/octet-stream");
        put("class", "application/octet-stream");
    }};
    private HashMap<String, ZipFile> repository = new HashMap<String, ZipFile>();
    private HashMap<String, String> repositoryFS = new HashMap<String, String>();
    private HashMap<String, HashMap<String, byte[]>> encrytions = new HashMap<String, HashMap<String, byte[]>>();
    private boolean quiet;    


    
    public void addZipFile(ZipFile zipFile, String resourceId) {
    	this.repository.put(resourceId, zipFile);
    	if (!this.isAlive()) { 
    		try {
				this.start();
//				Log.i("PageServer", "start at port:"+this.getListeningPort());
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
    
    public void setAESEncryptedItems(List<String> items, byte[] aesKey, String resourceId) {
    	HashMap<String, byte[]> encrypts = new HashMap<String, byte[]>();
    	for (String item : items) {
    		encrypts.put(item, aesKey);
    	}
    	this.encrytions.put(resourceId, encrypts);
    }
    
    
    
    public void addFSRoot(String root, String key) {
    	this.repositoryFS.put(key, root);
    	if (!this.isAlive()) {
    		try {
				this.start();
//				Log.i("PageServer", "start at port:"+this.getListeningPort());
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
    
    public void removeFile(String resourceId) {
    	this.repository.remove(resourceId);
    	this.repositoryFS.remove(resourceId);
    	this.encrytions.remove(resourceId);
    	if (this.repository.isEmpty() && this.repositoryFS.isEmpty()) {
			this.stop();
//			Log.i("PageServer", "stopped");
		}
    }

    public Response serve(IHTTPSession session) {
        Map<String, String> header = session.getHeaders();
        Map<String, String> parms = session.getParms();
        String uri = session.getUri();

        if (!quiet) {
            System.out.println(session.getMethod() + " '" + uri + "' ");

            Iterator<String> e = header.keySet().iterator();
            while (e.hasNext()) {
                String value = e.next();
                System.out.println("  HDR: '" + value + "' = '" + header.get(value) + "'");
            }
            e = parms.keySet().iterator();
            while (e.hasNext()) {
                String value = e.next();
                System.out.println("  PRM: '" + value + "' = '" + parms.get(value) + "'");
            }
        }

        return respond(Collections.unmodifiableMap(header), uri);
    }

    private Response respond(Map<String, String> header, String uri) {
        // Remove URL arguments
        uri = uri.trim().replace(File.separatorChar, '/');
        if (uri.indexOf('?') >= 0) {
            uri = uri.substring(0, uri.indexOf('?'));
        }

//        ZipEntry entry = this.queryEntry(uri);
//        if (null==entry) {
//            return createResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "Error 404, file not found.");
//        }
        InputStream fis;
		try {
			fis = this.queryInputStream(uri);
		} catch (IOException e) {
			e.printStackTrace();
			return createResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "Error 404, file not found.");
		}
        if (null==fis) {
            return createResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "Error 404, file not found.");
        }
        
        
        String mime = getMimeTypeForFile(uri);
        Response res = null;
        try {
            // Calculate etag
            String etag = Integer.toHexString(uri.hashCode());

            // Support (simple) skipping:
            long startFrom = 0;
            long endAt = -1;
            String range = header.get("range");
            if (range != null) {
                if (range.startsWith("bytes=")) {
                    range = range.substring("bytes=".length());
                    int minus = range.indexOf('-');
                    try {
                        if (minus > 0) {
                            startFrom = Long.parseLong(range.substring(0, minus));
                            endAt = Long.parseLong(range.substring(minus + 1));
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            // Change return code and add Content-Range header when skipping is requested
            long fileLen = fis.available();
            if (range != null && startFrom >= 0) {
                if (startFrom >= fileLen) {
                    res = createResponse(Response.Status.RANGE_NOT_SATISFIABLE, NanoHTTPD.MIME_PLAINTEXT, "");
                    res.addHeader("Content-Range", "bytes 0-0/" + fileLen);
                    res.addHeader("ETag", etag);
                } else {
                    if (endAt < 0) {
                        endAt = fileLen - 1;
                    }
                    long newLen = endAt - startFrom + 1;
                    if (newLen < 0) {
                        newLen = 0;
                    }

                    final long dataLen = newLen;
                    fis.skip(startFrom);

                    res = createResponse(Response.Status.PARTIAL_CONTENT, mime, fis);
                    res.addHeader("Content-Length", "" + dataLen);
                    res.addHeader("Content-Range", "bytes " + startFrom + "-" + endAt + "/" + fileLen);
                    res.addHeader("ETag", etag);
                }
            } else {
                if (etag.equals(header.get("if-none-match")))
                    res = createResponse(Response.Status.NOT_MODIFIED, mime, "");
                else {
                    res = createResponse(Response.Status.OK, mime, this.queryInputStream(uri));
                    res.addHeader("Content-Length", "" + fileLen);
                    res.addHeader("ETag", etag);
                }
            }
        } catch (IOException ioe) {
            res = createResponse(Response.Status.FORBIDDEN, NanoHTTPD.MIME_PLAINTEXT, "FORBIDDEN: Reading file failed.");
        }
        return res != null ? res : createResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "Error 404, file not found.");
    }


    // Get MIME type from file name extension, if possible
    private String getMimeTypeForFile(String uri) {
        int dot = uri.lastIndexOf('.');
        String mime = null;
        if (dot >= 0) {
            mime = MIME_TYPES.get(uri.substring(dot + 1).toLowerCase());
        }
        return mime == null ? MIME_DEFAULT_BINARY : mime;
    }

    // Announce that the file server accepts partial content requests
    private Response createResponse(Response.Status status, String mimeType, InputStream message) {
        Response res = new Response(status, mimeType, message);
        res.addHeader("Accept-Ranges", "bytes");
        return res;
    }

    // Announce that the file server accepts partial content requests
    private Response createResponse(Response.Status status, String mimeType, String message) {
        Response res = new Response(status, mimeType, message);
        res.addHeader("Accept-Ranges", "bytes");
        return res;
    }
    
    
    
//    private ZipEntry queryEntry(String path) {
//    	if (null!=path && path.length()>0 && path.charAt(0)=='/') {
//    		path = path.substring(1);
//    	}
//    	int slashIndex = path.indexOf('/');
//    	if (slashIndex>0) {
//    		String key = path.substring(0, slashIndex);
//    		String fileName = path.substring(slashIndex+1);
//    		ZipFile zipFile = this.repository.get(key);
//    		if (null != zipFile) {
//    			return zipFile.getEntry(fileName);
//    		}
//    	}
//    	return null;
//    }
    
//    static byte[] key = Base64.decode("XwSxkq+d94Ml43xha+5VrdyJqwwpaN33mvwz4b4a6Pk=",Base64.DEFAULT);
	static byte[] keyiv = { 0, 0, 0, 0, 0, 0, 0, 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 };

	private static byte[] InputStreamTOByte(InputStream in) throws IOException{  
        
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        byte[] data = new byte[1000];  
        int count = -1;  
        while((count = in.read(data,0,1000)) != -1)  
            outStream.write(data, 0, count);  
          
        data = null;  
        return outStream.toByteArray();  
    }  
     
    private InputStream queryInputStream(String path) throws IOException {
    	InputStream is = null;
    	if (null!=path && path.length()>0 && path.charAt(0)=='/') {
    		path = path.substring(1);
    	}
    	int slashIndex = path.indexOf('/');
    	if (slashIndex>0) {
    		String resId = path.substring(0, slashIndex);
    		String fileName = path.substring(slashIndex+1);
    		ZipFile zipFile = this.repository.get(resId);
    		if (null != zipFile) {
    			ZipEntry entry = zipFile.getEntry(fileName);
    			if (null != entry) {
    				is = zipFile.getInputStream(entry);
    			}
    		} else if (resId.equals("android_asset")) {
//    			is = this.context.getAssets().open(fileName);
    		} else {
    			String root = this.repositoryFS.get(resId);
    			if (null != root) {
    				if (root.charAt(root.length()-1)!='/') {
    					root = root+File.separator;
    				}
    				String filePath = root+fileName;
    				String assetMark = "/android_asset/";
    				int assetIndex = filePath.indexOf(assetMark);
    				if (assetIndex>=0) {
    					String assetPath = filePath.substring(assetIndex+assetMark.length());
//    					is = this.context.getAssets().open(assetPath);
    				} else {
    					is = new FileInputStream(new File(filePath));
    				}
    			}
    		}
    		if (null == is) {
    			if (path.indexOf("buy.html")>=0) {
    				String buyHtml = "<html><body><a href='EPUB-ACTION/buy'>ï¿?ï¿?</a></body></html>";;
    				is = new ByteArrayInputStream(buyHtml.getBytes()); 
    			}
    		}
    		
    		HashMap<String, byte[]> encrypts = this.encrytions.get(resId);
    		if (null != encrypts) {
    			byte[] aesKey = encrypts.get(fileName);
    			if (null != aesKey) {
    				byte[] fileContent = InputStreamTOByte(is);
    				Cipher cipher;
					try {
						cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
						IvParameterSpec ips = new IvParameterSpec(keyiv);
	    				cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(aesKey, "CBC"), ips);
	    				byte[] bOut = cipher.doFinal(fileContent);
	    				is = new ByteArrayInputStream(bOut);
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
					} catch (NoSuchPaddingException e) {
						e.printStackTrace();
					} catch (InvalidKeyException e) {
						e.printStackTrace();
					} catch (InvalidAlgorithmParameterException e) {
						e.printStackTrace();
					} catch (IllegalBlockSizeException e) {
						e.printStackTrace();
					} catch (BadPaddingException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
    			}
    		}
    	}
    	return is;
    }
    
    public InputStream getInputStream(String url) throws Exception {
    	String path = url;
    	if (path.indexOf("http")==0 || path.indexOf("file:")==0) {
    		path = (new URL(URLDecoder.decode(url, "UTF-8"))).getPath();
    	}
    	return this.queryInputStream(path);
    }
    
//	public WebResourceResponse getResoure(String url) throws Exception {
//    	InputStream s = this.getInputStream(url);
//    	if (null != s) {
//    		return new WebResourceResponse(this.getMimeTypeForFile(url), null, s);
//    	} else {
//    		return null;
//    	}
//    }
    
    public static final String PREFIX = "stream2file";
    public static final String SUFFIX = ".tmp";

    public static File stream2file (InputStream in) throws IOException {
        final File tempFile = File.createTempFile(PREFIX, SUFFIX);
        tempFile.deleteOnExit();
        OutputStream out = null;
        try {
            out = new FileOutputStream(tempFile);
            int BUFFER_SIZE = 16 * 1024;
    		byte buffer[] = new byte[BUFFER_SIZE];
    		int realLength;
    		 
    		while ((realLength = in.read(buffer)) > 0) {
    			out.write(buffer, 0, realLength);
    		}
    		return tempFile;
        } finally {
            if (out != null) {
              out.close();
            }
        }
    }
    
    public File getFile(String url) throws Exception {
    	InputStream s = this.getInputStream(url);
    	if (null != s) {
    		return stream2file(s);
    	} else {
    		return null;
    	}
    }
    
    //
    public String getTextFile(String url) throws Exception {
    	InputStream s = this.getInputStream(url);
    	if (null != s) {
    		InputStreamReader read = new InputStreamReader(s, "UTF-8");
			BufferedReader br = new BufferedReader(read);
			String valueString = null;
			StringBuffer sb = new StringBuffer();
			while ((valueString = br.readLine()) != null) {
				sb.append(valueString);
			}
			read.close();
			return sb.toString();
    	} else {
    		return null;
    	}
    }
}
