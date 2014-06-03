package pl.statedebug;

import com.intellij.debugger.actions.BaseValueAction;
import com.intellij.debugger.impl.DebuggerContextImpl;
import com.intellij.debugger.ui.impl.watch.DebuggerTreeNodeImpl;
import com.intellij.debugger.ui.impl.watch.ValueDescriptorImpl;
import com.intellij.openapi.project.Project;

public class StateToCodeAction extends BaseValueAction {

    @Override
    protected void processText(Project project, String text, DebuggerTreeNodeImpl node, DebuggerContextImpl debuggerContext) {
        ValueDescriptorImpl userObject = (ValueDescriptorImpl) node.getUserObject();
        String code = new ValueToCode().convert(userObject.getValue());
        System.out.println(code);
    }
}
