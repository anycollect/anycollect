package io.github.anycollect.extensions.common.expression.parser;

import io.github.anycollect.extensions.common.expression.ast.*;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public final class Parser {
    private final Tokenizer tokenizer;
    private final Map<String, FilterStrategy> filters;
    private Deque<Token> tokens;
    private Token lookahead;

    public Parser(final Tokenizer tokenizer, final Map<String, FilterStrategy> filters) {
        this.tokenizer = tokenizer;
        this.filters = new HashMap<>(filters);
    }

    public ValueExpressionNode parse(final String input) throws ParseException {
        tokens = new LinkedList<>(tokenizer.tokenize(input));
        lookahead = tokens.getFirst();
        ValueExpressionNode ret = expression();
        if (lookahead.getType() != TokenType.EPSILON) {
            throw new ParseException("unexpected symbol found " + lookahead.getSequence());
        }
        return ret;
    }

    private ValueExpressionNode expression() throws ParseException {
        // expression -> chain pipe_op
        ValueExpressionNode chain = chain();
        return pipeOp(new PipeExpressionNode(chain));
    }

    private ValueExpressionNode chain() throws ParseException {
        if (lookahead.getType() == TokenType.OPEN_BRACKET) {
            // part -> OPEN_BRACKET expression CLOSE_BRACKET
            nextToken();
            ValueExpressionNode node = expression();
            if (lookahead.getType() != TokenType.CLOSE_BRACKET) {
                throw new UnexpectedTokenParseException(lookahead, TokenType.CLOSE_BRACKET);
            }
            nextToken();
            return node;
        } else {
            return value();
        }
    }

    private PipeExpressionNode pipeOp(final PipeExpressionNode pipe) throws ParseException {
        if (lookahead.getType() == TokenType.PIPE) {
            // pipe_op -> PIPE filter pipe_op
            nextToken();
            FilterExpressionNode filter = filter();
            pipe.add(filter);
            pipeOp(pipe);
            return pipe;
        }
        // pipe_op -> EPSILON
        return pipe;
    }

    private ValueExpressionNode value() throws ParseException {
        if (lookahead.getType() == TokenType.DOUBLE_QUOTES) {
            // value -> DOUBLE_QUOTES value_op DOUBLE_QUOTES
            ComplexStringExpressionNode string = new ComplexStringExpressionNode();
            nextToken();
            valueOp(string);
            if (lookahead.getType() != TokenType.DOUBLE_QUOTES) {
                throw new IllegalArgumentException(lookahead.getSequence());
            }
            nextToken();
            return string;
        } else if (lookahead.getType() == TokenType.CONSTANT) {
            // value -> CONSTANT
            ConstantExpressionNode constant = new ConstantExpressionNode(lookahead.getSequence());
            nextToken();
            return constant;
        } else if (lookahead.getType() == TokenType.VARIABLE) {
            // value -> VARIABLE
            VariableExpressionNode variable = new VariableExpressionNode(lookahead.getSequence());
            nextToken();
            return variable;
        } else if (lookahead.getType() == TokenType.EPSILON) {
            throw new ParseException("unexpected end of expression");
        } else {
            throw new UnexpectedTokenParseException(lookahead,
                    TokenType.DOUBLE_QUOTES, TokenType.CONSTANT, TokenType.VARIABLE);
        }
    }

    private ComplexStringExpressionNode valueOp(final ComplexStringExpressionNode string) {
        if (lookahead.getType() == TokenType.CONSTANT) {
            // value_op -> CONSTANT value_op
            ConstantExpressionNode constant = new ConstantExpressionNode(lookahead.getSequence());
            string.add(constant);
            nextToken();
            valueOp(string);
            return string;
        } else if (lookahead.getType() == TokenType.VARIABLE) {
            // value_op -> VARIABLE value_op
            VariableExpressionNode variable = new VariableExpressionNode(lookahead.getSequence());
            string.add(variable);
            nextToken();
            valueOp(string);
            return string;
        } else {
            // value_op -> EPSILON
            return string;
        }
    }

    private FilterExpressionNode filter() throws ParseException {
        if (lookahead.getType() == TokenType.FILTER) {
            String filterName = lookahead.getSequence();
            FilterStrategy strategy = filters.get(filterName);
            nextToken();
            // filter -> FILTER OPEN_BRACKET arguments CLOSE_BRACKET
            if (lookahead.getType() == TokenType.OPEN_BRACKET) {
                nextToken();
                ArgumentsExpressionNode arguments = arguments();
                if (lookahead.getType() == TokenType.CLOSE_BRACKET) {
                    nextToken();
                    return new FilterExpressionNode(filterName, arguments, strategy);
                } else {
                    throw new UnexpectedTokenParseException(lookahead, TokenType.CLOSE_BRACKET);
                }
            } else {
                // filter -> FILTER
                return new FilterExpressionNode(filterName, strategy);
            }
        } else {
            throw new UnexpectedTokenParseException(lookahead, TokenType.FILTER);
        }
    }

    private ArgumentsExpressionNode arguments() throws ParseException {
        // arguments -> argument arguments_op
        ArgumentExpressionNode argument = argument();
        return argumentsOp(new ArgumentsExpressionNode(argument));
    }

    private ArgumentsExpressionNode argumentsOp(final ArgumentsExpressionNode arguments) throws ParseException {
        if (lookahead.getType() == TokenType.COLON) {
            // arguments_op -> COLON arguments_op
            nextToken();
            ArgumentExpressionNode argument = argument();
            arguments.add(argument);
            argumentsOp(arguments);
            return arguments;
        }
        // arguments_op -> EPSILON
        return arguments;
    }

    private ArgumentExpressionNode argument() throws ParseException {
        // argument -> expression
        return new ArgumentExpressionNode(expression());
    }

    private void nextToken() {
        tokens.pop();
        if (tokens.isEmpty()) {
            lookahead = Token.of(TokenType.EPSILON, "");
        } else {
            lookahead = tokens.getFirst();
        }
    }
}
