package org.basex.query.flwor;

import static org.basex.query.QueryText.*;
import java.io.IOException;
import org.basex.query.QueryContext;
import org.basex.query.QueryException;
import org.basex.query.expr.*;
import org.basex.query.func.*;
import org.basex.query.item.*;
import org.basex.data.*;
import org.basex.io.serial.*;
import org.basex.query.util.*;
import org.basex.util.InputInfo;
import org.basex.util.TokenBuilder;

/**
 * Implementation of the group by clause.
 *
 * @author BaseX Team 2005-12, BSD License
 * @author Michael Seiferle
 */
public final class Group extends ExprInfo {
  /** Group by specification. */
  private final Spec[] groupby;
  /** Non-grouping variables. */
  final Var[][] nongroup;
  /** Grouping partition. **/
  GroupPartition gp;
  /** Input info. */
  private final InputInfo input;

  /**
   * Constructor.
   * @param ii input info
   * @param gb group by expression
   * @param ng non-grouping variables and their copies
   */
  public Group(final InputInfo ii, final Spec[] gb, final Var[][] ng) {
    input = ii;
    groupby = gb;
    nongroup = ng;
  }

  /**
   * Initializes the grouping partition.
   * @param ob order by specifier
   */
  void init(final Order ob) {
    gp = new GroupPartition(groupby, nongroup, ob, input);
  }

  @Override
  public void plan(final Serializer ser) throws IOException {
    ser.openElement(this);
    for(int o = 0; o != groupby.length; ++o) groupby[o].plan(ser);
    ser.closeElement();
  }

  @Override
  public String toString() {
    return new TokenBuilder(' ' + GROUP + ' ' + BY + ' ').
      addSep(groupby, SEP).toString();
  }

  /**
   * Grouping spec.
   *
   * @author BaseX Team 2005-12, BSD License
   * @author Leo Woerteler
   */
  public static class Spec extends Single {
    /** Grouping variable. */
    public final Var grp;

    /**
     * Constructor.
     * @param ii input info
     * @param gv grouping variable
     * @param e grouping expression
     */
    public Spec(final InputInfo ii, final Var gv, final Expr e) {
      super(ii, e);
      grp = gv;
    }

    @Override
    public void plan(final Serializer ser) throws IOException {
      ser.openElement(this);
      grp.plan(ser);
      expr.plan(ser);
      ser.closeElement();
    }

    @Override
    public String toString() {
      return grp + " " + ASSIGN + ' ' + expr;
    }

    @Override
    public Item item(final QueryContext ctx, final InputInfo ii) throws QueryException {
      return value(ctx).item(ctx, ii);
    }

    @Override
    public Value value(final QueryContext ctx) throws QueryException {
      final Value val = expr.value(ctx);
      if(val.size() > 1) throw Err.XGRP.thrw(input);
      return val.isEmpty() ? val : StandardFunc.atom(val.itemAt(0), input);
    }
  }
}
