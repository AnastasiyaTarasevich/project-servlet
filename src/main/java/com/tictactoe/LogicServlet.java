package com.tictactoe;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;


@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {

    private final MiniMax minimax=new MiniMax();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession currentSession=req.getSession();
        Field field=extractField(currentSession);

        Boolean gameOver=(Boolean) currentSession.getAttribute("gameOver");
        if (gameOver != null && gameOver) {
            // Если игра завершена, перенаправляем на игровую страницу
            resp.sendRedirect("/index.jsp");
            return;
        }
        int index = getSelectedIndex(req);

        Sign currentSign=field.getField().get(index);

        if(Sign.EMPTY!=currentSign)
        {
            RequestDispatcher dispatcher= getServletContext().getRequestDispatcher("/index.jsp");
            dispatcher.forward(req,resp);
            return;
        }
        field.getField().put(index,Sign.CROSS);
        if (checkWin(resp, currentSession, field)) {
            return;
        }


        int emptyFieldIndex= field.getEmptyFieldIndex();
        if (emptyFieldIndex >= 0) {
            // Применяем Minimax для выбора хода компьютера
            int bestMove = minimax.findBestMove(field);
            field.getField().put(bestMove, Sign.NOUGHT);

            if (checkWin(resp, currentSession, field)) {
                return;
            }
        }else {

            currentSession.setAttribute("draw", true);
            List<Sign> data = field.getFieldData();
            currentSession.setAttribute("data", data);
            resp.sendRedirect("/index.jsp");
            return;
        }

        List<Sign> data=field.getFieldData();

        currentSession.setAttribute("data",data);
        currentSession.setAttribute("field",field);
        resp.sendRedirect("/index.jsp");
    }


    private int getSelectedIndex(HttpServletRequest request) {
        String click = request.getParameter("click");
        boolean isNumeric = click.chars().allMatch(Character::isDigit);
        return isNumeric ? Integer.parseInt(click) : 0;
    }

    private Field extractField(HttpSession httpSession) {
        Object fieldAttribute = httpSession.getAttribute("field");
        if (Field.class != fieldAttribute.getClass())
        {
            httpSession.invalidate();
            throw new RuntimeException("Session is broken");
        }
        return (Field) fieldAttribute;
    }

    private boolean checkWin(HttpServletResponse response, HttpSession session, Field field) throws IOException
    {
        Sign winner=field.checkWin();
        if (Sign.CROSS == winner || Sign.NOUGHT == winner) {
            session.setAttribute("winner",winner);
            List<Sign> data=field.getFieldData();
            session.setAttribute("gameOver", true); // Устанавливаем флаг завершения игры
            session.setAttribute("data",data);

            response.sendRedirect("/index.jsp");
            return true;
        }
        return false;
    }
}