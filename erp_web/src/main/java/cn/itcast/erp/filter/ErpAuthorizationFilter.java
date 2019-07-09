package cn.itcast.erp.filter;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ErpAuthorizationFilter extends AuthorizationFilter {
    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        Subject subject = getSubject(servletRequest, servletResponse);
        String[] perms = (String[]) o;
        if (perms == null && perms.length == 0) {
            return true;
        }
        for (String p: perms) {
            if (subject.isPermitted(p)) {
                return true;
            }
        }
        return false;
    }
}
