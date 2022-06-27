/*
 * Copyright (c) 2016 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gaugeLib.hansolo.medusa;

import gaugeLib.hansolo.medusa.Gauge.NeedleType;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;


/**
 * Created by hansolo on 22.02.16.
 */
public enum Needle {
      INSTANCE;

      Needle() {

      }

      /**
       * Returns the path for the given NeedleType.
       *
       * @param PATH
       * @param NEEDLE_WIDTH
       * @param NEEDLE_HEIGHT
       * @param NEEDLE_TYPE
       * @param TICK_LABEL_LOCATION
       * @return the path for the given NeedleType
       */
      public Path getPath(final Path PATH, final double NEEDLE_WIDTH, final double NEEDLE_HEIGHT, final NeedleType NEEDLE_TYPE, final TickLabelLocation TICK_LABEL_LOCATION) {
            PATH.getElements().clear();
            switch (NEEDLE_TYPE) {
                  case BIG:
                        switch (TICK_LABEL_LOCATION) {
                              case OUTSIDE:
                                    PATH.getElements().add(new MoveTo(0.0, 0.927710843373494 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.0, 0.9698795180722891 * NEEDLE_HEIGHT, 0.20833333333333334 * NEEDLE_WIDTH, NEEDLE_HEIGHT, 0.5 * NEEDLE_WIDTH, NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.7916666666666666 * NEEDLE_WIDTH, NEEDLE_HEIGHT, NEEDLE_WIDTH, 0.9698795180722891 * NEEDLE_HEIGHT, NEEDLE_WIDTH, 0.927710843373494 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(NEEDLE_WIDTH, 0.9096385542168675 * NEEDLE_HEIGHT, 0.5 * NEEDLE_WIDTH, 0.0, 0.5 * NEEDLE_WIDTH, 0.0));
                                    PATH.getElements().add(new CubicCurveTo(0.5 * NEEDLE_WIDTH, 0.0, 0.0, 0.9096385542168675 * NEEDLE_HEIGHT, 0.0, 0.927710843373494 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new ClosePath());
                                    break;
                              case INSIDE:
                              default:
                                    PATH.getElements().add(new MoveTo(0.0, 0.9396984924623115 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.0, 0.9748743718592965 * NEEDLE_HEIGHT, 0.20833333333333334 * NEEDLE_WIDTH, NEEDLE_HEIGHT, 0.5 * NEEDLE_WIDTH, NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.7916666666666666 * NEEDLE_WIDTH, NEEDLE_HEIGHT, NEEDLE_WIDTH, 0.9748743718592965 * NEEDLE_HEIGHT, NEEDLE_WIDTH, 0.9396984924623115 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(NEEDLE_WIDTH, 0.9246231155778895 * NEEDLE_HEIGHT, 0.5 * NEEDLE_WIDTH, 0.0, 0.5 * NEEDLE_WIDTH, 0.0));
                                    PATH.getElements().add(new CubicCurveTo(0.5 * NEEDLE_WIDTH, 0.0, 0.0, 0.9246231155778895 * NEEDLE_HEIGHT, 0.0, 0.9396984924623115 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new ClosePath());
                                    break;
                        }
                        break;
                  case FAT:
                        PATH.getElements().add(new MoveTo(0.275 * NEEDLE_WIDTH, 0.7029702970297029 * NEEDLE_HEIGHT));
                        PATH.getElements().add(new CubicCurveTo(0.275 * NEEDLE_WIDTH, 0.6287128712871287 * NEEDLE_HEIGHT, 0.375 * NEEDLE_WIDTH, 0.5693069306930693 * NEEDLE_HEIGHT, 0.5 * NEEDLE_WIDTH, 0.5693069306930693 * NEEDLE_HEIGHT));
                        PATH.getElements().add(new CubicCurveTo(0.625 * NEEDLE_WIDTH, 0.5693069306930693 * NEEDLE_HEIGHT, 0.725 * NEEDLE_WIDTH, 0.6287128712871287 * NEEDLE_HEIGHT, 0.725 * NEEDLE_WIDTH, 0.7029702970297029 * NEEDLE_HEIGHT));
                        PATH.getElements().add(new CubicCurveTo(0.725 * NEEDLE_WIDTH, 0.7772277227722773 * NEEDLE_HEIGHT, 0.625 * NEEDLE_WIDTH, 0.8366336633663366 * NEEDLE_HEIGHT, 0.5 * NEEDLE_WIDTH, 0.8366336633663366 * NEEDLE_HEIGHT));
                        PATH.getElements().add(new CubicCurveTo(0.375 * NEEDLE_WIDTH, 0.8366336633663366 * NEEDLE_HEIGHT, 0.275 * NEEDLE_WIDTH, 0.7772277227722773 * NEEDLE_HEIGHT, 0.275 * NEEDLE_WIDTH, 0.7029702970297029 * NEEDLE_HEIGHT));
                        PATH.getElements().add(new ClosePath());
                        PATH.getElements().add(new MoveTo(0.0, 0.7029702970297029 * NEEDLE_HEIGHT));
                        PATH.getElements().add(new CubicCurveTo(0.0, 0.8663366336633663 * NEEDLE_HEIGHT, 0.225 * NEEDLE_WIDTH, NEEDLE_HEIGHT, 0.5 * NEEDLE_WIDTH, NEEDLE_HEIGHT));
                        PATH.getElements().add(new CubicCurveTo(0.775 * NEEDLE_WIDTH, NEEDLE_HEIGHT, NEEDLE_WIDTH, 0.8663366336633663 * NEEDLE_HEIGHT, NEEDLE_WIDTH, 0.7029702970297029 * NEEDLE_HEIGHT));
                        PATH.getElements().add(new CubicCurveTo(NEEDLE_WIDTH, 0.5396039603960396 * NEEDLE_HEIGHT, 0.5 * NEEDLE_WIDTH, 0.0, 0.5 * NEEDLE_WIDTH, 0.0));
                        PATH.getElements().add(new CubicCurveTo(0.5 * NEEDLE_WIDTH, 0.0, 0.0, 0.5396039603960396 * NEEDLE_HEIGHT, 0.0, 0.7029702970297029 * NEEDLE_HEIGHT));
                        PATH.getElements().add(new ClosePath());
                        break;
                  case SCIENTIFIC:
                        switch (TICK_LABEL_LOCATION) {
                              case OUTSIDE:
                                    PATH.getElements().add(new MoveTo(0.023809523809523808 * NEEDLE_WIDTH, 0.9422222222222222 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(-0.047619047619047616 * NEEDLE_WIDTH, 0.9777777777777777 * NEEDLE_HEIGHT, 0.023809523809523808 * NEEDLE_WIDTH, NEEDLE_HEIGHT, 0.23809523809523808 * NEEDLE_WIDTH, NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.23809523809523808 * NEEDLE_WIDTH, NEEDLE_HEIGHT, 0.7619047619047619 * NEEDLE_WIDTH, NEEDLE_HEIGHT, 0.7619047619047619 * NEEDLE_WIDTH, NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.9761904761904762 * NEEDLE_WIDTH, NEEDLE_HEIGHT, 1.0476190476190477 * NEEDLE_WIDTH, 0.9777777777777777 * NEEDLE_HEIGHT, 0.9761904761904762 * NEEDLE_WIDTH, 0.9422222222222222 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.9761904761904762 * NEEDLE_WIDTH, 0.9422222222222222 * NEEDLE_HEIGHT, 0.6904761904761905 * NEEDLE_WIDTH, 0.8533333333333334 * NEEDLE_HEIGHT, 0.6904761904761905 * NEEDLE_WIDTH, 0.8533333333333334 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.6666666666666666 * NEEDLE_WIDTH, 0.84 * NEEDLE_HEIGHT, 0.6428571428571429 * NEEDLE_WIDTH, 0.7866666666666666 * NEEDLE_HEIGHT, 0.6190476190476191 * NEEDLE_WIDTH, 0.7555555555555555 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.8095238095238095 * NEEDLE_WIDTH, 0.7466666666666667 * NEEDLE_HEIGHT, 0.9285714285714286 * NEEDLE_WIDTH, 0.72 * NEEDLE_HEIGHT, 0.9285714285714286 * NEEDLE_WIDTH, 0.6844444444444444 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.9285714285714286 * NEEDLE_WIDTH, 0.6444444444444445 * NEEDLE_HEIGHT, 0.7857142857142857 * NEEDLE_WIDTH, 0.6177777777777778 * NEEDLE_HEIGHT, 0.5714285714285714 * NEEDLE_WIDTH, 0.6088888888888889 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.5714285714285714 * NEEDLE_WIDTH, 0.6088888888888889 * NEEDLE_HEIGHT, 0.5476190476190477 * NEEDLE_WIDTH, 0.08 * NEEDLE_HEIGHT, 0.5476190476190477 * NEEDLE_WIDTH, 0.08 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new LineTo(0.5 * NEEDLE_WIDTH, 0.0));
                                    PATH.getElements().add(new LineTo(0.4523809523809524 * NEEDLE_WIDTH, 0.08 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.4523809523809524 * NEEDLE_WIDTH, 0.08 * NEEDLE_HEIGHT, 0.42857142857142855 * NEEDLE_WIDTH, 0.6088888888888889 * NEEDLE_HEIGHT, 0.42857142857142855 * NEEDLE_WIDTH, 0.6088888888888889 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.21428571428571427 * NEEDLE_WIDTH, 0.6177777777777778 * NEEDLE_HEIGHT, 0.07142857142857142 * NEEDLE_WIDTH, 0.6444444444444445 * NEEDLE_HEIGHT, 0.07142857142857142 * NEEDLE_WIDTH, 0.6844444444444444 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.07142857142857142 * NEEDLE_WIDTH, 0.72 * NEEDLE_HEIGHT, 0.19047619047619047 * NEEDLE_WIDTH, 0.7466666666666667 * NEEDLE_HEIGHT, 0.38095238095238093 * NEEDLE_WIDTH, 0.7555555555555555 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.35714285714285715 * NEEDLE_WIDTH, 0.7866666666666666 * NEEDLE_HEIGHT, 0.3333333333333333 * NEEDLE_WIDTH, 0.84 * NEEDLE_HEIGHT, 0.30952380952380953 * NEEDLE_WIDTH, 0.8533333333333334 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.30952380952380953 * NEEDLE_WIDTH, 0.8533333333333334 * NEEDLE_HEIGHT, 0.023809523809523808 * NEEDLE_WIDTH, 0.9422222222222222 * NEEDLE_HEIGHT, 0.023809523809523808 * NEEDLE_WIDTH, 0.9422222222222222 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new ClosePath());
                                    break;
                              case INSIDE:
                              default:
                                    PATH.getElements().add(new MoveTo(0.023809523809523808 * NEEDLE_WIDTH, 0.9496124031007752 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(-0.047619047619047616 * NEEDLE_WIDTH, 0.9806201550387597 * NEEDLE_HEIGHT, 0.023809523809523808 * NEEDLE_WIDTH, NEEDLE_HEIGHT, 0.23809523809523808 * NEEDLE_WIDTH, NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.23809523809523808 * NEEDLE_WIDTH, NEEDLE_HEIGHT, 0.7619047619047619 * NEEDLE_WIDTH, NEEDLE_HEIGHT, 0.7619047619047619 * NEEDLE_WIDTH, NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.9761904761904762 * NEEDLE_WIDTH, NEEDLE_HEIGHT, 1.0476190476190477 * NEEDLE_WIDTH, 0.9806201550387597 * NEEDLE_HEIGHT, 0.9761904761904762 * NEEDLE_WIDTH, 0.9496124031007752 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.9761904761904762 * NEEDLE_WIDTH, 0.9496124031007752 * NEEDLE_HEIGHT, 0.6904761904761905 * NEEDLE_WIDTH, 0.872093023255814 * NEEDLE_HEIGHT, 0.6904761904761905 * NEEDLE_WIDTH, 0.872093023255814 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.6666666666666666 * NEEDLE_WIDTH, 0.8604651162790697 * NEEDLE_HEIGHT, 0.6428571428571429 * NEEDLE_WIDTH, 0.813953488372093 * NEEDLE_HEIGHT, 0.6190476190476191 * NEEDLE_WIDTH, 0.7868217054263565 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.8095238095238095 * NEEDLE_WIDTH, 0.7790697674418605 * NEEDLE_HEIGHT, 0.9285714285714286 * NEEDLE_WIDTH, 0.7558139534883721 * NEEDLE_HEIGHT, 0.9285714285714286 * NEEDLE_WIDTH, 0.7248062015503876 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.9285714285714286 * NEEDLE_WIDTH, 0.689922480620155 * NEEDLE_HEIGHT, 0.7857142857142857 * NEEDLE_WIDTH, 0.6666666666666666 * NEEDLE_HEIGHT, 0.5714285714285714 * NEEDLE_WIDTH, 0.6589147286821705 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.5714285714285714 * NEEDLE_WIDTH, 0.6589147286821705 * NEEDLE_HEIGHT, 0.5476190476190477 * NEEDLE_WIDTH, 0.06976744186046512 * NEEDLE_HEIGHT, 0.5476190476190477 * NEEDLE_WIDTH, 0.06976744186046512 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new LineTo(0.5 * NEEDLE_WIDTH, 0.0));
                                    PATH.getElements().add(new LineTo(0.4523809523809524 * NEEDLE_WIDTH, 0.06976744186046512 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.4523809523809524 * NEEDLE_WIDTH, 0.06976744186046512 * NEEDLE_HEIGHT, 0.42857142857142855 * NEEDLE_WIDTH, 0.6589147286821705 * NEEDLE_HEIGHT, 0.42857142857142855 * NEEDLE_WIDTH, 0.6589147286821705 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.21428571428571427 * NEEDLE_WIDTH, 0.6666666666666666 * NEEDLE_HEIGHT, 0.07142857142857142 * NEEDLE_WIDTH, 0.689922480620155 * NEEDLE_HEIGHT, 0.07142857142857142 * NEEDLE_WIDTH, 0.7248062015503876 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.07142857142857142 * NEEDLE_WIDTH, 0.7558139534883721 * NEEDLE_HEIGHT, 0.19047619047619047 * NEEDLE_WIDTH, 0.7790697674418605 * NEEDLE_HEIGHT, 0.38095238095238093 * NEEDLE_WIDTH, 0.7868217054263565 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.35714285714285715 * NEEDLE_WIDTH, 0.813953488372093 * NEEDLE_HEIGHT, 0.3333333333333333 * NEEDLE_WIDTH, 0.8604651162790697 * NEEDLE_HEIGHT, 0.30952380952380953 * NEEDLE_WIDTH, 0.872093023255814 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.30952380952380953 * NEEDLE_WIDTH, 0.872093023255814 * NEEDLE_HEIGHT, 0.023809523809523808 * NEEDLE_WIDTH, 0.9496124031007752 * NEEDLE_HEIGHT, 0.023809523809523808 * NEEDLE_WIDTH, 0.9496124031007752 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new ClosePath());
                                    break;
                        }
                        break;
                  case AVIONIC:
                        switch (TICK_LABEL_LOCATION) {
                              case OUTSIDE:
                                    PATH.getElements().add(new MoveTo(0.3333333333333333 * NEEDLE_WIDTH, 0.05825242718446602 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.3333333333333333 * NEEDLE_WIDTH, 0.05825242718446602 * NEEDLE_HEIGHT, 0.3333333333333333 * NEEDLE_WIDTH, 0.6941747572815534 * NEEDLE_HEIGHT, 0.3333333333333333 * NEEDLE_WIDTH, 0.6941747572815534 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.125 * NEEDLE_WIDTH, 0.6990291262135923 * NEEDLE_HEIGHT, 0.0, 0.7233009708737864 * NEEDLE_HEIGHT, 0.0, 0.7475728155339806 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.0, 0.7766990291262136 * NEEDLE_HEIGHT, 0.16666666666666666 * NEEDLE_WIDTH, 0.7961165048543689 * NEEDLE_HEIGHT, 0.375 * NEEDLE_WIDTH, 0.8058252427184466 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.375 * NEEDLE_WIDTH, 0.8058252427184466 * NEEDLE_HEIGHT, 0.375 * NEEDLE_WIDTH, 0.9368932038834952 * NEEDLE_HEIGHT, 0.375 * NEEDLE_WIDTH, 0.9368932038834952 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.2916666666666667 * NEEDLE_WIDTH, 0.941747572815534 * NEEDLE_HEIGHT, 0.20833333333333334 * NEEDLE_WIDTH, 0.9514563106796117 * NEEDLE_HEIGHT, 0.20833333333333334 * NEEDLE_WIDTH, 0.9660194174757282 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.20833333333333334 * NEEDLE_WIDTH, 0.9854368932038835 * NEEDLE_HEIGHT, 0.3333333333333333 * NEEDLE_WIDTH, NEEDLE_HEIGHT, 0.5 * NEEDLE_WIDTH, NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.6666666666666666 * NEEDLE_WIDTH, NEEDLE_HEIGHT, 0.7916666666666666 * NEEDLE_WIDTH, 0.9854368932038835 * NEEDLE_HEIGHT, 0.7916666666666666 * NEEDLE_WIDTH, 0.9660194174757282 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.7916666666666666 * NEEDLE_WIDTH, 0.9514563106796117 * NEEDLE_HEIGHT, 0.7083333333333334 * NEEDLE_WIDTH, 0.941747572815534 * NEEDLE_HEIGHT, 0.625 * NEEDLE_WIDTH, 0.9368932038834952 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.625 * NEEDLE_WIDTH, 0.9368932038834952 * NEEDLE_HEIGHT, 0.625 * NEEDLE_WIDTH, 0.8058252427184466 * NEEDLE_HEIGHT, 0.625 * NEEDLE_WIDTH, 0.8058252427184466 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.8333333333333334 * NEEDLE_WIDTH, 0.7961165048543689 * NEEDLE_HEIGHT, NEEDLE_WIDTH, 0.7766990291262136 * NEEDLE_HEIGHT, NEEDLE_WIDTH, 0.7475728155339806 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(NEEDLE_WIDTH, 0.7233009708737864 * NEEDLE_HEIGHT, 0.875 * NEEDLE_WIDTH, 0.6990291262135923 * NEEDLE_HEIGHT, 0.6666666666666666 * NEEDLE_WIDTH, 0.6941747572815534 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.6666666666666666 * NEEDLE_WIDTH, 0.6941747572815534 * NEEDLE_HEIGHT, 0.6666666666666666 * NEEDLE_WIDTH, 0.05825242718446602 * NEEDLE_HEIGHT, 0.6666666666666666 * NEEDLE_WIDTH, 0.05825242718446602 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new LineTo(0.5 * NEEDLE_WIDTH, 0.0));
                                    PATH.getElements().add(new LineTo(0.3333333333333333 * NEEDLE_WIDTH, 0.05825242718446602 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new ClosePath());
                                    break;
                              case INSIDE:
                              default:
                                    PATH.getElements().add(new MoveTo(0.3333333333333333 * NEEDLE_WIDTH, 0.0502092050209205 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.3333333333333333 * NEEDLE_WIDTH, 0.0502092050209205 * NEEDLE_HEIGHT, 0.3333333333333333 * NEEDLE_WIDTH, 0.7364016736401674 * NEEDLE_HEIGHT, 0.3333333333333333 * NEEDLE_WIDTH, 0.7364016736401674 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.125 * NEEDLE_WIDTH, 0.7405857740585774 * NEEDLE_HEIGHT, 0.0, 0.7615062761506276 * NEEDLE_HEIGHT, 0.0, 0.7824267782426778 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.0, 0.8075313807531381 * NEEDLE_HEIGHT, 0.16666666666666666 * NEEDLE_WIDTH, 0.8242677824267782 * NEEDLE_HEIGHT, 0.375 * NEEDLE_WIDTH, 0.8326359832635983 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.375 * NEEDLE_WIDTH, 0.8326359832635983 * NEEDLE_HEIGHT, 0.375 * NEEDLE_WIDTH, 0.9456066945606695 * NEEDLE_HEIGHT, 0.375 * NEEDLE_WIDTH, 0.9456066945606695 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.2916666666666667 * NEEDLE_WIDTH, 0.9497907949790795 * NEEDLE_HEIGHT, 0.20833333333333334 * NEEDLE_WIDTH, 0.9581589958158996 * NEEDLE_HEIGHT, 0.20833333333333334 * NEEDLE_WIDTH, 0.9707112970711297 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.20833333333333334 * NEEDLE_WIDTH, 0.9874476987447699 * NEEDLE_HEIGHT, 0.3333333333333333 * NEEDLE_WIDTH, NEEDLE_HEIGHT, 0.5 * NEEDLE_WIDTH, NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.6666666666666666 * NEEDLE_WIDTH, NEEDLE_HEIGHT, 0.7916666666666666 * NEEDLE_WIDTH, 0.9874476987447699 * NEEDLE_HEIGHT, 0.7916666666666666 * NEEDLE_WIDTH, 0.9707112970711297 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.7916666666666666 * NEEDLE_WIDTH, 0.9581589958158996 * NEEDLE_HEIGHT, 0.7083333333333334 * NEEDLE_WIDTH, 0.9497907949790795 * NEEDLE_HEIGHT, 0.625 * NEEDLE_WIDTH, 0.9456066945606695 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.625 * NEEDLE_WIDTH, 0.9456066945606695 * NEEDLE_HEIGHT, 0.625 * NEEDLE_WIDTH, 0.8326359832635983 * NEEDLE_HEIGHT, 0.625 * NEEDLE_WIDTH, 0.8326359832635983 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.8333333333333334 * NEEDLE_WIDTH, 0.8242677824267782 * NEEDLE_HEIGHT, NEEDLE_WIDTH, 0.8075313807531381 * NEEDLE_HEIGHT, NEEDLE_WIDTH, 0.7824267782426778 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(NEEDLE_WIDTH, 0.7615062761506276 * NEEDLE_HEIGHT, 0.875 * NEEDLE_WIDTH, 0.7405857740585774 * NEEDLE_HEIGHT, 0.6666666666666666 * NEEDLE_WIDTH, 0.7364016736401674 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new CubicCurveTo(0.6666666666666666 * NEEDLE_WIDTH, 0.7364016736401674 * NEEDLE_HEIGHT, 0.6666666666666666 * NEEDLE_WIDTH, 0.0502092050209205 * NEEDLE_HEIGHT, 0.6666666666666666 * NEEDLE_WIDTH, 0.0502092050209205 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new LineTo(0.5 * NEEDLE_WIDTH, 0.0));
                                    PATH.getElements().add(new LineTo(0.3333333333333333 * NEEDLE_WIDTH, 0.0502092050209205 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new ClosePath());
                                    break;
                        }
                        break;
                  case VARIOMETER:
                        switch (TICK_LABEL_LOCATION) {
                              case OUTSIDE:
                                    PATH.getElements().add(new MoveTo(0.0, 0.07792207792207792 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new LineTo(0.0, NEEDLE_HEIGHT));
                                    PATH.getElements().add(new LineTo(NEEDLE_WIDTH, NEEDLE_HEIGHT));
                                    PATH.getElements().add(new LineTo(NEEDLE_WIDTH, 0.07792207792207792 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new LineTo(0.5 * NEEDLE_WIDTH, 0.0));
                                    PATH.getElements().add(new LineTo(0.0, 0.07792207792207792 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new ClosePath());
                                    break;
                              case INSIDE:
                              default:
                                    PATH.getElements().add(new MoveTo(0.0, 0.06417112299465241 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new LineTo(0.0, NEEDLE_HEIGHT));
                                    PATH.getElements().add(new LineTo(NEEDLE_WIDTH, NEEDLE_HEIGHT));
                                    PATH.getElements().add(new LineTo(NEEDLE_WIDTH, 0.06417112299465241 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new LineTo(0.5 * NEEDLE_WIDTH, 0.0));
                                    PATH.getElements().add(new LineTo(0.0, 0.06417112299465241 * NEEDLE_HEIGHT));
                                    PATH.getElements().add(new ClosePath());
                                    break;
                        }
                        break;
                  case STANDARD:
                  default:
                        PATH.getElements().add(new MoveTo(0.25 * NEEDLE_WIDTH, 0.025423728813559324 * NEEDLE_HEIGHT));
                        PATH.getElements().add(new CubicCurveTo(0.25 * NEEDLE_WIDTH, 0.00847457627118644 * NEEDLE_HEIGHT, 0.375 * NEEDLE_WIDTH, 0, 0.5 * NEEDLE_WIDTH, 0));
                        PATH.getElements().add(new CubicCurveTo(0.625 * NEEDLE_WIDTH, 0, 0.75 * NEEDLE_WIDTH, 0.00847457627118644 * NEEDLE_HEIGHT, 0.75 * NEEDLE_WIDTH, 0.025423728813559324 * NEEDLE_HEIGHT));
                        PATH.getElements().add(new CubicCurveTo(0.75 * NEEDLE_WIDTH, 0.025423728813559324 * NEEDLE_HEIGHT, NEEDLE_WIDTH, NEEDLE_HEIGHT, NEEDLE_WIDTH, NEEDLE_HEIGHT));
                        PATH.getElements().add(new LineTo(0, NEEDLE_HEIGHT));
                        PATH.getElements().add(new CubicCurveTo(0, NEEDLE_HEIGHT, 0.25 * NEEDLE_WIDTH, 0.025423728813559324 * NEEDLE_HEIGHT, 0.25 * NEEDLE_WIDTH, 0.025423728813559324 * NEEDLE_HEIGHT));
                        PATH.getElements().add(new ClosePath());
                        break;
            }
            return PATH;
      }
}
