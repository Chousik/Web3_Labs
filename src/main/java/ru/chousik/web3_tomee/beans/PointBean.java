package ru.chousik.web3_tomee.beans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import ru.chousik.web3_tomee.database.DatabaseService;
import ru.chousik.web3_tomee.models.Point;
import ru.chousik.web3_tomee.services.PointsService;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Named("pointBean")
@Getter
@Setter
@ViewScoped
public class PointBean implements Serializable {
    private double selectedX = 0;
    private double selectedY;
    private double selectedR = 1;
    private List<Point> points;
    private Point point;
    private String sessionId;

    @Inject
    private PointsService pointsService;
    @Inject
    private DatabaseService databaseService;

    @PostConstruct
    public void loadPointsFromDb() {
        HttpSession session = getCurrentSession();
        if (session != null) {
            sessionId = session.getId();
        }
        points = databaseService.getPoints(sessionId);
        Collections.reverse(points);
    }

    public void checkPoint() {
        long startTime = System.nanoTime();
        Point point = new Point();
        point.setX(selectedX);
        point.setY(selectedY);
        point.setR(selectedR);
        point.setSessionId(sessionId);

        if (pointsService.valid(point)) {
            point.setInFlag(pointsService.check(point));
            point.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            point.setExecutionTime(System.nanoTime() - startTime);
            PrimeFaces.current().executeScript("printDot(" + point.getX() + ", " + point.getY() + ", " + point.isInFlag() + ");");
            this.addPoint(point);
        }
    }

    private void addPoint(Point point) {
        databaseService.addPoint(point);
        points.add(0, point);
        this.point = point;
    }

    private HttpSession getCurrentSession() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            return (HttpSession) facesContext.getExternalContext().getSession(true);
        }
        return null;
    }
}