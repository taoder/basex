package org.basex.query.up.primitives;

import static org.basex.query.QueryText.*;

import org.basex.data.Data;
import org.basex.query.QueryException;
import org.basex.query.item.DBNode;
import org.basex.query.item.Nod;
import org.basex.query.util.Err;
import org.basex.util.Token;

/**
 * Replace element content primitive.  
 *
 * @author Workgroup DBIS, University of Konstanz 2005-09, ISC License
 * @author Lukas Kircher
 */
public final class ReplaceElemContentPrimitive extends UpdatePrimitive {
  /** Replacing text node. */ 
  public byte[] r;

  /**
   * Constructor.
   * @param n target node
   * @param tn replacing content
   */
  public ReplaceElemContentPrimitive(final Nod n, final byte[] tn) {
    super(n);
    r = tn;
  }

  @SuppressWarnings("unused")
  @Override
  public void check() throws QueryException {
  }

  @SuppressWarnings("unused")
  @Override
  public void apply(final int add) throws QueryException {
    if(!(node instanceof DBNode)) return;
    Token.string(r);
    final DBNode n = (DBNode) node;
    final int p = n.pre + add;
    final Data d = n.data;
    final int j = p + d.attSize(p, Data.ELEM);
    int i = p + d.size(p, Data.ELEM) - 1;
    while(i >= j) d.delete(i--);
    d.insert(j, p, r, Data.TEXT);
  }

  @Override
  public void merge(final UpdatePrimitive p) throws QueryException {
    if(mult) Err.or(UPTRGMULT, node);
    mult = true;
  }

  @Override
  public Type type() {
    return Type.REPLACEELEMCONT;
  }

}
