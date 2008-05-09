package org.intellij.stripes.reference;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMember;
import com.intellij.psi.impl.search.AnnotatedMembersSearcher;
import com.intellij.psi.search.searches.AnnotatedMembersSearch;
import com.intellij.util.Processor;
import com.intellij.openapi.util.text.StringUtil;
import org.intellij.stripes.util.StripesConstants;

import java.util.Map;
import java.util.Hashtable;

public class UrlBindingSearcher extends AnnotatedMembersSearcher {
    public class UrlBindingProcessor implements Processor<PsiMember> {
        private Map<String, PsiClass> bindings = new Hashtable<String, PsiClass>();

        public boolean process(PsiMember member) {
            if (member instanceof PsiClass) {
                PsiAnnotation ann = member.getModifierList().findAnnotation(StripesConstants.URL_BINDING_ANNOTATION);
                if (null != ann) {
                    PsiAnnotationMemberValue value = ann.findAttributeValue("value");
                    if (value != null) bindings.put(StringUtil.stripQuotesAroundValue(value.getText()), (PsiClass) member);
                }
            }
            return true;
        }

        public Map<String, PsiClass> getBindings() {
            return bindings;
        }
    }

    private AnnotatedMembersSearch.Parameters params;

    public UrlBindingSearcher(PsiClass urlBindingCls) {
        this.params = new AnnotatedMembersSearch.Parameters(urlBindingCls, urlBindingCls.getUseScope());
    }

    public Map<String, PsiClass> execute() {
        UrlBindingProcessor proc = new UrlBindingProcessor();
        super.execute(params, proc);
        return proc.getBindings();
    }
}
