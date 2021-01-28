package com.example.zipunzipgzipgunzip;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.*;

@Slf4j
@SpringBootApplication
public class ZipunzipgzipgunzipApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ZipunzipgzipgunzipApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
		String source_filepath = "processamentoTemp/2020-12-04.json";
		String gzip_filepath = "processamentoTemp/2020-12-04.json.gz";
		String decopressed_filepath = "processamentoTemp/2020-12-04-out-gz.json";


        //zipFile();
        //log.info("Inicio Compressao BZIP2");
        //createBzipFile();
        //log.info("FIM Compressao BZIP2");
        log.info("Inixio GZIP");
		gzipFile(source_filepath, gzip_filepath);
		unGunzipFile(gzip_filepath, decopressed_filepath);
		log.info("Fim gzip");
    }

    public void zipFile() throws IOException {
        String sourceFile = "test1.txt";
        FileOutputStream fos = new FileOutputStream("compressed.zip");
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        File fileToZip = new File(sourceFile);
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        zipOut.close();
        fis.close();
        fos.close();
    }

    public void unzipFile() throws IOException {
        String fileZip = "src/main/resources/unzipTest/compressed.zip";
        File destDir = new File("src/main/resources/unzipTest");
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            File newFile = newFile(destDir, zipEntry);
            if (zipEntry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    throw new IOException("Failed to create directory " + newFile);
                }
            } else {
                // fix for Windows-created archives
                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Failed to create directory " + parent);
                }

                // write file content
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    public void createBzipFile() throws IOException {
        InputStream in = Files.newInputStream(Paths.get("processamentoTemp/2020-12-04.json"));
        OutputStream fout = Files.newOutputStream(Paths.get("processamentoTemp/2020-12-04.json.bz2"));
        BufferedOutputStream out = new BufferedOutputStream(fout);
        BZip2CompressorOutputStream bzOut = new BZip2CompressorOutputStream(out);
        int buffersize = 1024;
        final byte[] buffer = new byte[buffersize];
        int n = 0;
        while (-1 != (n = in.read(buffer))) {
            bzOut.write(buffer, 0, n);
        }
        bzOut.close();
        in.close();
    }

    public void desBZipFile() throws IOException {

        InputStream fin = Files.newInputStream(Paths.get("archive.json.bz2"));
        BufferedInputStream in = new BufferedInputStream(fin);
        OutputStream out = Files.newOutputStream(Paths.get("archive.json"));
        BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(in);
        int buffersize = 1024;
        final byte[] buffer = new byte[buffersize];
        int n = 0;
        while (-1 != (n = bzIn.read(buffer))) {
            out.write(buffer, 0, n);
        }
        out.close();
        bzIn.close();

    }

	public void gzipFile(String source_filepath, String destinaton_zip_filepath) {

		byte[] buffer = new byte[1024];

		try {

			FileOutputStream fileOutputStream = new FileOutputStream(
					destinaton_zip_filepath);

			GZIPOutputStream gzipOuputStream = new GZIPOutputStream(
					fileOutputStream);

			FileInputStream fileInput = new FileInputStream(source_filepath);

			int bytes_read;

			while ((bytes_read = fileInput.read(buffer)) > 0) {
				gzipOuputStream.write(buffer, 0, bytes_read);
			}

			fileInput.close();

			gzipOuputStream.finish();
			gzipOuputStream.close();

			System.out.println("The file was compressed successfully!");

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void unGunzipFile(String compressedFile, String decompressedFile) {

		byte[] buffer = new byte[1024];

		try {

			FileInputStream fileIn = new FileInputStream(compressedFile);

			GZIPInputStream gZIPInputStream = new GZIPInputStream(fileIn);

			FileOutputStream fileOutputStream = new FileOutputStream(decompressedFile);

			int bytes_read;

			while ((bytes_read = gZIPInputStream.read(buffer)) > 0) {

				fileOutputStream.write(buffer, 0, bytes_read);
			}

			gZIPInputStream.close();
			fileOutputStream.close();

			System.out.println("The file was decompressed successfully!");

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}


}
