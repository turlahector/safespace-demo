package com.safespace;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Autowired
	DataSource datasource;
	
	//@Autowired
	//MyUserDetailsService userDetailsService;
	 
	@Autowired
	CustomSuccessHandler customSuccessHandler;
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/admin/**").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers("/account/**").hasAnyAuthority("USER");
        http.authorizeRequests().antMatchers("/exchange/**").hasAnyAuthority("USER");
        http.authorizeRequests().antMatchers("/wallet/**").hasAnyAuthority("USER");
        http
                .formLogin().failureUrl("/account?error")
                	.loginPage("/account")
                	.successHandler(customSuccessHandler)
                	.permitAll()
                	.and()
                	.exceptionHandling().accessDeniedPage("/admin/login?accessDenied").and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/")
                .permitAll();
    }
	
	
 
    protected void configure(AuthenticationManagerBuilder auth, HttpSecurity http) throws Exception {
    	//auth.userDetailsService(userDetailsService);
    	auth.jdbcAuthentication().dataSource(datasource)
		.usersByUsernameQuery(
			"SELECT user_name,password, enabled FROM user WHERE user_name=?")
		.authoritiesByUsernameQuery(
			"SELECT u.user_name, r.name role FROM user u INNER JOIN role r USING(role_id) WHERE u.user_name=?");
    	http.cors().and().antMatcher("/.well-known/**");
    }
    
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/api/**"); //excluding all API for now. Handle security later
    }
    

}
