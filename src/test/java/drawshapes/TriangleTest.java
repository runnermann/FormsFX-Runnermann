package drawshapes;

import draw.shapes.FMTriangle;
import uicontrols.UIColors;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TriangleTest {

    @Test
    public void testTriangle() {

        FMTriangle fmTri;
        double anchorX = 30;
        double anchorY = 20;
        double deltaX = 50;
        double deltaY = 50;

        fmTri = new FMTriangle(
                anchorX,
                anchorY,
                deltaX,
                deltaY,
                3,
                UIColors.HIGHLIGHT_PINK,
                "0x00000000",
                0
        );

        assertEquals(30, fmTri.getX());
        assertEquals(70, fmTri.getY());
        assertEquals(80, fmTri.getX2());
        assertEquals(70, fmTri.getY2());
        assertEquals(55, fmTri.getX3());
        assertEquals(20, fmTri.getY3());
    }

}
