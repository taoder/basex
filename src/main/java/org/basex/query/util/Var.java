package org.basex.query.util;

import org.basex.query.*;
import java.io.IOException;
import org.basex.io.serial.Serializer;
import org.basex.data.ExprInfo;
import org.basex.query.item.QNm;
import org.basex.query.item.SeqType;
import org.basex.util.Token;
import org.basex.util.TokenBuilder;

/**
 * Variable expression.
 *
 * @author BaseX Team 2005-12, BSD License
 * @author Christian Gruen
 * @author Leo Woerteler
 */
public final class Var extends ExprInfo {
  /**
   * Variable kinds.
   * @author Leo Woerteler
   */
  public static enum VarKind {
    /** Local variable. */
    LOCAL,
    /** Global variable. */
    GLOBAL,
    /** Function parameter. */
    FUNC_PARAM;
  };

  /** Variable name. */
  public final QNm name;
  /** Variable ID. */
  public final int id;

  /** Stack slot number. */
  public int slot = -1;

  /** Expected result size. */
  public long size;

  /** Expected return type, {@code null} if not important. */
  private SeqType ret;
  /** Actual return type (by type inference). */
  private SeqType type;
  /** Flag for global variables. */
  public final VarKind kind;

  /**
   * Constructor.
   * @param ctx query context, used for generating a variable ID
   * @param n variable name, {@code null} for unnamed variable
   * @param typ expected type, {@code null} for no check
   * @param k kind of variable
   */
  public Var(final QueryContext ctx, final QNm n, final SeqType typ, final VarKind k) {
    name = n;
    ret = typ;
    type = typ != null ? typ : SeqType.ITEM_ZM;
    id = ctx.varIDs++;
    kind = k;
    size = type.occ();
  }

  /**
   * Constructor for local variables.
   * @param ctx query context, used for generating a variable ID
   * @param n variable name, {@code null} for unnamed variable
   * @param typ expected type, {@code null} for no check
   */
  public Var(final QueryContext ctx, final QNm n, final SeqType typ) {
    this(ctx, n, typ, VarKind.LOCAL);
  }

  /**
   * Creates a copy of this variable with the given type and casting behavior.
   * @param ctx query context for new variable ID
   * @param t type to be assigned
   * @return copy of the variable
   */
  public Var withType(final QueryContext ctx, final SeqType t) {
    return new Var(ctx, name, t, kind);
  }

  /**
   * Type of values bound to this variable.
   * @return (non-{@code null}) type
   */
  public SeqType type() {
    return type;
  }

  /**
   * Tries to refine the compile-time type of this variable through the type of the bound
   * expression.
   * @param t type of the bound expression
   * @throws QueryException if the types are incompatible
   */
  public void refineType(final SeqType t) throws QueryException {
    // [LW] insert checks here
    type = t;
  }

  /**
   * Checks if this variable is global.
   * @return result of check
   */
  public boolean global() {
    return kind == VarKind.GLOBAL;
  }

  /**
   * Determines if this variable checks the type of the expression bound to it.
   * @return {@code true} if the type is checked or promoted, {@code false} otherwise
   */
  public boolean checksType() {
    return ret != null;
  }

  /**
   * Checks whether the given variable is identical to this one, i.e. has the
   * same ID.
   * @param v variable to check
   * @return {@code true}, if the IDs are equal, {@code false} otherwise
   */
  public boolean is(final Var v) {
    return id == v.id;
  }

  @Override
  public void plan(final Serializer ser) throws IOException {
    ser.emptyElement(this, QueryText.NAM, Token.token(toString()),
        QueryText.ID, Token.token(id));
  }

  @Override
  public String toString() {
    final TokenBuilder tb = new TokenBuilder();
    if(name != null) {
      tb.add(QueryText.DOLLAR).add(name.string());
      if(type != null) tb.add(' ' + QueryText.AS);
    }
    if(type != null) tb.add(" " + type);
    return tb.toString();
  }

  @Override
  public boolean equals(final Object obj) {
    return obj instanceof Var && is((Var) obj);
  }

  @Override
  public int hashCode() {
    return id;
  }

  /**
   * Sets the return type of this variable.
   * @param rt return type
   * @throws QueryException if the return type is incompatible
   */
  public void setRetType(final SeqType rt) throws QueryException {
    refineType(ret);
    ret = rt;
  }
}
