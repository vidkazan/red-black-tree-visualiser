NAME = rbtVisualiser

NAME_EDIT = cgi-bin/edit_tree.out

NAME_CLEAR = cgi-bin/clear_tree.out

SRCS_EDIT =	src/rbt_cgi.cpp

SRCS_CLEAR = src/clear_cgi.cpp

HDRS =  src/treeApi.hpp

OBJS_EDIT = $(SRCS_EDIT:.cpp=.o)

OBJS_CLEAR = $(SRCS_CLEAR:.cpp=.o)

FLAGS = -O3 -g -Wall -Wextra -Werror -std=c++98

COMP = g++

%.o: %.cpp		$(HDRS)
				$(COMP) $(FLAGS) -c $< -o $@

all:			$(NAME_CLEAR) $(NAME_EDIT)

$(NAME_EDIT):		$(OBJS_EDIT)
					$(COMP) $(FLAGS) $(OBJS_EDIT) -o $@

$(NAME_CLEAR):		$(OBJS_CLEAR)
					$(COMP) $(FLAGS) $(OBJS_CLEAR) -o $@

clean:
				@rm -f $(OBJS_EDIT) $(OBJS_CLEAR)

fclean:			clean
				@rm -f $(NAME_EDIT) $(NAME_CLEAR)

re:				fclean all


.PHONY:			all clean fclean re
