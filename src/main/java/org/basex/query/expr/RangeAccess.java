package org.basex.query.expr;

import static org.basex.query.QueryText.*;
import static org.basex.util.Token.*;

import java.io.*;
import java.util.*;

import org.basex.data.*;
import org.basex.index.*;
import org.basex.index.IndexToken.IndexType;
import org.basex.io.serial.*;
import org.basex.query.*;
import org.basex.query.item.*;
import org.basex.query.iter.*;
import org.basex.query.util.*;
import org.basex.util.*;

/**
 * This index class retrieves range values from the index.
 *
 * @author BaseX Team 2005-12, BSD License
 * @author Christian Gruen
 */
public final class RangeAccess extends IndexAccess {
  /** Index type. */
  final NumericRange ind;

  /**
   * Constructor.
   * @param ii input info
   * @param t index reference
   * @param ic index context
   */
  RangeAccess(final InputInfo ii, final NumericRange t, final IndexContext ic) {
    super(ic, ii);
    ind = t;
  }

  @Override
  public AxisIter iter(final QueryContext ctx) {
    final Data data = ictx.data;
    final byte kind = ind.type() == IndexType.TEXT ? Data.TEXT : Data.ATTR;

    return new AxisIter() {
      final IndexIterator it = data.iter(ind);
      @Override
      public ANode next() {
        return it.more() ? new DBNode(data, it.next(), kind) : null;
      }
    };
  }

  @Override
  public void plan(final Serializer ser) throws IOException {
    ser.emptyElement(this, DATA, token(ictx.data.meta.name),
        MIN, token(ind.min), MAX, token(ind.max),
        TYP, token(ind.type.toString()));
  }

  @Override
  public String toString() {
    return new TokenBuilder().add(DB).add(':').
      add(ind.type().toString().toLowerCase(Locale.ENGLISH)).add("-range(").
      addExt(ind.min).add(SEP).addExt(ind.max).add(')').toString();
  }
}
