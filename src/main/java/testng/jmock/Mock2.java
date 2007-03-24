package testng.jmock;

import org.jmock.Mock;

/**
 * User: piotrga
 * Date: 2007-03-20
 * Time: 09:16:48
 */
public class Mock2<T> extends Mock {

    public Mock2(Class mockedType) {
        super(mockedType);
    }


    public T proxy() {
        return (T) super.proxy();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
