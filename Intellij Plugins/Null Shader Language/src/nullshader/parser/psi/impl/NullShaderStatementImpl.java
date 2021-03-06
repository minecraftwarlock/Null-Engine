// This is a generated file. Not intended for manual editing.
package nullshader.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static nullshader.parser.psi.NullShaderTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import nullshader.parser.psi.*;

public class NullShaderStatementImpl extends ASTWrapperPsiElement implements NullShaderStatement {

  public NullShaderStatementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull NullShaderVisitor visitor) {
    visitor.visitStatement(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof NullShaderVisitor) accept((NullShaderVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public NullShaderCompoundStatement getCompoundStatement() {
    return findChildByClass(NullShaderCompoundStatement.class);
  }

  @Override
  @Nullable
  public NullShaderSimpleStatement getSimpleStatement() {
    return findChildByClass(NullShaderSimpleStatement.class);
  }

}
