package fileops;

public enum VertxLink {
      // request for creating a member account
      REQ_MEMBER() {
            final String endpoint = "/P01/HF25XZ";

            public String getEndPoint() {
                  return endpoint;
            }

            public String getLink() {
                  return DOMAIN + endpoint;
            }
      },
      // hyperlink address
      ONBOARD() {
            final String endpoint = "/P01/F06H21";

            public String getEndPoint() {
                  return endpoint;
            }

            public String getLink() {
                  return DOMAIN + endpoint;
            }
      },
      // calls for a deck...
      GET_RESOURCE() {
            final String endpoint = "/P01/A77J30";

            public String getEndPoint() {
                  return endpoint;
            }

            public String getLink() {
                  return DOMAIN + endpoint;
            }
      },
      REQ_PURCHASE() {
            final String endpoint = "/P01/F7G4l5";

            public String getEndPoint() {
                  return endpoint;
            }

            public String getLink() {
                  return DOMAIN + endpoint;
            }
      },
      CANCELLED() {
            final String endpoint = "/cancel";

            public String getEndPoint() {
                  return endpoint;
            }

            public String getLink() {
                  return DOMAIN + endpoint;
            }
      },
      SUCCESS() {
            final String endpoint = "/P01/A309FF";

            public String getEndPoint() {
                  return endpoint;
            }

            public String getLink() {
                  return DOMAIN + endpoint;
            }
      },
      REQ_ACCT() {
            final String endpoint = "/req-acct";

            public String getEndPoint() {
                  return endpoint;
            }

            public String getLink() {
                  return DOMAIN + endpoint;
            }
      },
      SUBSCRIPT_CANCEL() {
            final String endpoint = "/P01/CA25XZ";

            public String getEndPoint() {
                  return endpoint;
            }

            public String getLink() {
                  return DOMAIN + endpoint;
            }
      },
      CANCEL_POLICY() {
            final String endpoint = "/pay/making-sales";

            public String getEndPoint() {
                  return endpoint;
            }

            public String getLink() {
                  return DOMAIN + endpoint;
            }
      };


      // --------------------------------- --------------------------------- //
      //                             COMMON
      // --------------------------------- --------------------------------- //

      private static final String DOMAIN = Connect.LINK.getLink();

      VertxLink() { /* empty constructor */ }

      public abstract String getLink();

      /**
       * Does not include the website.
       *
       * @return Returns the file extension for the endPoint. I.e. /IDK/server-endpoint
       */
      public abstract String getEndPoint();

}
