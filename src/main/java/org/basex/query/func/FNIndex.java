package org.basex.query.func;

import static org.basex.query.QueryText.*;
import static org.basex.util.Token.*;
import java.util.Arrays;
import org.basex.core.Main;
import org.basex.query.QueryException;
import org.basex.query.expr.Expr;
import org.basex.query.util.Err;
import org.basex.util.Levenshtein;
import org.basex.util.TokenSet;

/**
 * Global expression context.
 *
 * @author Workgroup DBIS, University of Konstanz 2005-10, ISC License
 * @author Christian Gruen
 */
public final class FNIndex extends TokenSet {
  /** Function classes. */
  private FunDef[] funcs;
  /** Singleton instance. */
  private static FNIndex instance = new FNIndex();

  /**
   * Gets the function instance.
   * @return instance
   */
  public static FNIndex get() {
    return instance;
  }

  /**
   * Constructor, registering XQuery functions.
   */
  private FNIndex() {
    funcs = new FunDef[CAP];
    for(final FunDef def : FunDef.values()) {
      final String dsc = def.desc;
      final byte[] key = token(dsc.substring(0, dsc.indexOf("(")));
      final int i = add(key);
      if(i < 0) Main.notexpected("Function defined twice:" + def);
      funcs[i] = def;
    }
  }

  /**
   * Returns the specified function.
   * @param name function name
   * @param uri function uri
   * @param args optional arguments
   * @return function instance
   * @throws QueryException query exception
   */
  public Fun get(final byte[] name, final byte[] uri, final Expr[] args)
      throws QueryException {

    final int id = id(name);
    if(id != 0) {
      try {
        // create function
        final FunDef fl = funcs[id];
        if(!eq(fl.uri, uri)) return null;

        final Fun f = fl.func.newInstance();
        f.init(fl, args);
        // check number of arguments
        if(args.length < fl.min || fl.max >= fl.min && args.length > fl.max)
          Err.or(XPARGS, fl);
        return f;
      } catch(final QueryException ex) {
        throw ex;
      } catch(final Exception ex) {
        Main.notexpected("Can't run " + string(name));
      }
    }
    return null;
  }

  /**
   * Finds similar function names for throwing an error message.
   * @param name function name
   * @throws QueryException query exception
   */
  public void error(final byte[] name) throws QueryException {
    // check similar predefined function
    final byte[] nm = lc(name);
    final Levenshtein ls = new Levenshtein();
    for(int k = 1; k < size; k++) {
      if(ls.similar(nm, lc(keys[k]), 0)) Err.or(FUNSIMILAR, name, keys[k]);
    }
  }

  @Override
  protected void rehash() {
    super.rehash();
    funcs = Arrays.copyOf(funcs, size << 1);
  }
}