// This is a generated file. Not intended for manual editing.
package nullshader.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface NullShaderRelationalExpr extends NullShaderExpr {

  @NotNull
  List<NullShaderExpr> getExprList();

  @NotNull
  NullShaderRelationalOp getRelationalOp();

}
