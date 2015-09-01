package pl.matsuo.gitlab.file;


import java.io.InputStream;


/**
 * Created by marek on 29.08.15.
 */
public interface FileConverter<E> {


  E convert(InputStream stream);


  E convert(String text);
}

